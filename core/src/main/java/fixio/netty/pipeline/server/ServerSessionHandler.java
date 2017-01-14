/*
 * Copyright 2014 The FIX.io Project
 *
 * The FIX.io Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package fixio.netty.pipeline.server;

import fixio.events.LogonEvent;
import fixio.fixprotocol.FieldType;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import fixio.fixprotocol.FixMessageBuilderImpl;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.session.FixSession;
import fixio.handlers.FixApplication;
import fixio.netty.pipeline.AbstractSessionHandler;
import fixio.netty.pipeline.FixClock;
import fixio.netty.pipeline.SessionRepository;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServerSessionHandler extends AbstractSessionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerSessionHandler.class);
    private final FixAuthenticator authenticator;
    private final int heartbeatIntervalSec = 30;

    public ServerSessionHandler(FixApplication fixApplication,
                                FixAuthenticator authenticator,
                                SessionRepository sessionRepository) {
        super(fixApplication, FixClock.systemUTC(), sessionRepository);
        assert (authenticator != null) : "FixAuthenticator is required for ServerSessionHandler";
        this.authenticator = authenticator;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("Connection established. Waiting for Logon.");
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    private FixMessageBuilder createLogonResponse() {
        FixMessageBuilder logonResponse = new FixMessageBuilderImpl(MessageTypes.LOGON);
        logonResponse.add(FieldType.HeartBtInt, heartbeatIntervalSec);
        return logonResponse;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        LOGGER.debug("Message Received: {}", msg);

        FixSession fixSession = getSession(ctx);
        final FixMessageHeader header = msg.getHeader();
        if (MessageTypes.LOGON.equals(header.getMessageType())) {

            LOGGER.debug("Logon request: {}", msg);
            if (fixSession != null) {
                throw new IllegalStateException("Duplicate Logon Request. Session Already Established.");
            } else {
                if (authenticator.authenticate(msg)) {
                    fixSession = initSession(ctx, header);

                    final int msgSeqNum = header.getMsgSeqNum();
                    int expectedMsgSeqNum = -1;

                    boolean seqTooHigh = false;
                    if (!fixSession.checkAndIncrementIncomingSeqNum(msgSeqNum)) {
                        expectedMsgSeqNum = fixSession.getNextIncomingMessageSeqNum();
                        if (msgSeqNum < expectedMsgSeqNum) {
                            sendLogoutAndClose(ctx, fixSession, "Sequence Number Too Low. Expected = " + expectedMsgSeqNum);
                            return;
                        } else {
                            seqTooHigh = true;
                        }
                    }

                    final FixMessageBuilder logonResponse = createLogonResponse();
                    prepareMessageToSend(ctx, fixSession, logonResponse);
                    LOGGER.info("Sending Logon Response: {}", logonResponse);
                    ctx.write(logonResponse);

                    if (seqTooHigh) {
                        assert (expectedMsgSeqNum > 0);
                        FixMessageBuilder resendRequest = new FixMessageBuilderImpl(MessageTypes.RESEND_REQUEST);
                        resendRequest.add(FieldType.BeginSeqNo, expectedMsgSeqNum);
                        resendRequest.add(FieldType.EndSeqNo, msgSeqNum - 1);
                        prepareMessageToSend(ctx, fixSession, resendRequest);
                        ctx.write(resendRequest);
                    }
                    ctx.flush();
                    out.add(new LogonEvent(fixSession));

                } else {
                    //If the authentication (of the session initiator's logon message) fails,
                    // the session acceptor should shut down the connection.
                    ctx.close();
                }
            }
        } else {
            super.decode(ctx, msg, out);
        }
    }

    private FixSession initSession(ChannelHandlerContext ctx, FixMessageHeader header) {
        FixSession session = getSessionRepository().getOrCreateSession(header);

        session.setNextOutgoingMessageSeqNum(1);

        setSession(ctx, session);
        LOGGER.info("Fix Session Established.");
        return session;
    }

    private void sendLogoutAndClose(ChannelHandlerContext ctx, FixSession session, String text) throws Exception {
        //rejected logon

        FixMessageBuilderImpl logout = new FixMessageBuilderImpl(MessageTypes.LOGOUT);
        if (text != null) {
            logout.add(58, text);
        }

        prepareMessageToSend(ctx, session, logout);
        ctx.writeAndFlush(logout).addListener(ChannelFutureListener.CLOSE);
    }
}
