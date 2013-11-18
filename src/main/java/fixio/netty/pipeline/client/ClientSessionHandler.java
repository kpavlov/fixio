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
import fixio.fixprotocol.*;
import fixio.netty.pipeline.AbstractSessionHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClientSessionHandler extends AbstractSessionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClientSessionHandler.class);
    private final FixSessionSettingsProvider sessionSettingsProvider;
    private volatile FixSession pendingSession;

    public ClientSessionHandler(FixSessionSettingsProvider settingsProvider) {
        sessionSettingsProvider = settingsProvider;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        final FixMessageHeader header = msg.getHeader();
        if (MessageTypes.LOGON.equals(header.getMessageType())) {
            if (sessionHolder.compareAndSet(null, pendingSession)) {
                getLogger().info("Fix Session Established.");
                incomingSeqNum.compareAndSet(1, header.getMsgSeqNum() + 1);
                LogonEvent logonEvent = new LogonEvent();
                out.add(logonEvent);
                return;
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

        pendingSession = createSession(sessionSettingsProvider);
        pendingSession.prepareOutgoing(logonRequest);
        //updateFixMessageHeader(logonRequest);
        getLogger().info("Sending Logon: {}", logonRequest);

        ctx.writeAndFlush(logonRequest);
    }

    private FixSession createSession(FixSessionSettingsProvider settingsProvider) {
        outgoingSeqNum.set(settingsProvider.getMsgSeqNum());
        incomingSeqNum.set(1);

        FixSession session = FixSession.newBuilder()
                .beginString(settingsProvider.getBeginString())
                .senderCompId(settingsProvider.getSenderCompID())
                .senderSubId(settingsProvider.getSenderSubID())
                .targetCompId(settingsProvider.getTargetCompID())
                .targetSubId(settingsProvider.getTargetSubID())
                .build();
        session.setNextOutgoingMessageSeqNum(settingsProvider.getMsgSeqNum());
        return session;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
