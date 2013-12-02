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

package fixio.netty.pipeline.client;

import fixio.events.LogonEvent;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.SimpleFixMessage;
import fixio.fixprotocol.session.FixSession;
import fixio.netty.pipeline.AbstractSessionHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClientSessionHandler extends AbstractSessionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClientSessionHandler.class);
    private final FixSessionSettingsProvider sessionSettingsProvider;

    public ClientSessionHandler(FixSessionSettingsProvider settingsProvider) {
        sessionSettingsProvider = settingsProvider;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        final FixMessageHeader header = msg.getHeader();
        FixSession session = getSession(ctx);
        if (MessageTypes.LOGON.equals(header.getMessageType())) {
            if (session != null) {
                getLogger().info("Fix Session Established.");
                if (session.checkIncomingSeqNum(header.getMsgSeqNum())) {
                    LogonEvent logonEvent = new LogonEvent();
                    out.add(logonEvent);
                    return;
                } else {
                    throw new IllegalStateException("Duplicate Logon Request. Session Already Established.");
                }

            } else {
                throw new IllegalStateException("Duplicate Logon Request. Session Already Established.");
            }
        }
        super.decode(ctx, msg, out);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        getLogger().info("Connection established, starting Client FIX session.");

        SimpleFixMessage logonRequest = new SimpleFixMessage(MessageTypes.LOGON);

        FixSession pendingSession = createSession(sessionSettingsProvider);
        setSession(ctx, pendingSession);
        pendingSession.prepareOutgoing(logonRequest);
        //updateFixMessageHeader(logonRequest);
        getLogger().info("Sending Logon: {}", logonRequest);

        ctx.writeAndFlush(logonRequest);
    }

    private FixSession createSession(FixSessionSettingsProvider settingsProvider) {

        int nextIncomingSeqNum = 0;
        if (settingsProvider.resetMsgSeqNum()) {
            nextIncomingSeqNum = 1;
        }

        FixSession session = FixSession.newBuilder()
                .beginString(settingsProvider.getBeginString())
                .senderCompId(settingsProvider.getSenderCompID())
                .senderSubId(settingsProvider.getSenderSubID())
                .targetCompId(settingsProvider.getTargetCompID())
                .targetSubId(settingsProvider.getTargetSubID())
                .nextIncomingSeqNum(nextIncomingSeqNum)
                .build();
        session.setNextOutgoingMessageSeqNum(settingsProvider.getMsgSeqNum());
        return session;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
