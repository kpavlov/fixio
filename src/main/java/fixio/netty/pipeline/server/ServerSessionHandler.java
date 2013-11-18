/*
 * Copyright 2013 The FIX.io Project
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
import fixio.fixprotocol.*;
import fixio.netty.pipeline.AbstractSessionHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServerSessionHandler extends AbstractSessionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerSessionHandler.class);
    private final FixAuthenticator authenticator;
    private int heartbeatIntervalSec = 30;

    public ServerSessionHandler(FixAuthenticator authenticator) {
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

    private FixMessage createLogonResponse() {
        SimpleFixMessage logonResponse = new SimpleFixMessage(MessageTypes.LOGON);
        logonResponse.add(FieldType.HeartBtInt, heartbeatIntervalSec);
        return logonResponse;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        LOGGER.debug("Message Received: {}", msg);

        final FixMessageHeader header = msg.getHeader();
        if (MessageTypes.LOGON.equals(header.getMessageType())) {
            LOGGER.debug("Logon request: {}", msg);
            if (sessionHolder.get() != null) {
                throw new IllegalStateException("Duplicate Logon Request. Session Already Established.");
            } else {
                if (authenticator.authenticate(header)) {
                    initSession(header);

                    int msgSeqNum = header.getMsgSeqNum();
                    final int expectedMsgSeqNum = incomingSeqNum.getAndIncrement();
                    if (msgSeqNum < expectedMsgSeqNum) {
                        sendLogoutAndClose(ctx, "Incoming Sequence Number < Expected = " + expectedMsgSeqNum);
                        return;
                    }

                    final FixMessage logonResponse = createLogonResponse();
                    updateFixMessageHeader(logonResponse);
                    LOGGER.info("Sending Logon Response: {}", logonResponse);
                    ctx.write(logonResponse);

                    if (msgSeqNum > expectedMsgSeqNum) {
                        FixMessage resendRequest = new SimpleFixMessage(MessageTypes.RESEND_REQUEST);
                        updateFixMessageHeader(resendRequest);
                        ctx.write(resendRequest);
                    }
                    ctx.flush();
                    out.add(new LogonEvent());

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

    private void initSession(FixMessageHeader header) {
        FixSession session = FixSession.newBuilder()
                .beginString(header.getBeginString())
                .senderCompId(header.getTargetCompID())
                .senderSubId(header.getTargetSubID())
                .targetCompId(header.getSenderCompID())
                .targetSubId(header.getTargetSubID())
                .build();
        session.setNextOutgoingMessageSeqNum(1);

        sessionHolder.compareAndSet(null, session);
        LOGGER.info("Fix Session Established.");
    }

    private void sendLogoutAndClose(ChannelHandlerContext ctx, String text) {
        //rejected logon

        SimpleFixMessage logout = new SimpleFixMessage(MessageTypes.LOGOUT);

        ctx.writeAndFlush(logout).addListener(ChannelFutureListener.CLOSE);
    }
}
