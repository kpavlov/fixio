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

package fixio.netty.pipeline.client;

import fixio.events.LogonEvent;
import fixio.fixprotocol.*;
import fixio.fixprotocol.session.FixSession;
import fixio.handlers.FixClientApplication;
import fixio.netty.pipeline.AbstractSessionHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClientSessionHandler extends AbstractSessionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClientSessionHandler.class);
    private final FixSessionSettingsProvider sessionSettingsProvider;
    private final MessageSequenceProvider messageSequenceProvider;
    private final FixClientApplication fixClientApplication;

    public ClientSessionHandler(FixSessionSettingsProvider settingsProvider,
                                MessageSequenceProvider messageSequenceProvider, FixClientApplication fixClientApplication) {
        this.fixClientApplication = fixClientApplication;
        assert (settingsProvider != null) : "FixSessionSettingsProvider is expected.";
        this.sessionSettingsProvider = settingsProvider;
        this.messageSequenceProvider = messageSequenceProvider;
    }

    public ClientSessionHandler(FixSessionSettingsProvider settingsProvider, FixClientApplication fixClientApplication) {
        this(settingsProvider, StatelessMessageSequenceProvider.getInstance(), fixClientApplication);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        final FixMessageHeader header = msg.getHeader();
        FixSession session = getSession(ctx);
        if (MessageTypes.LOGON.equals(header.getMessageType())) {
            if (session != null) {
                getLogger().info("Fix Session Established.");
                if (session.checkIncomingSeqNum(header.getMsgSeqNum())) {
                    LogonEvent logonEvent = new LogonEvent(session);
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

        FixMessageBuilder logonRequest = new FixMessageBuilderImpl(MessageTypes.LOGON);

        FixSession pendingSession = createSession(sessionSettingsProvider);
        setSession(ctx, pendingSession);
        updateFixMessageHeader(pendingSession, logonRequest);

        // callback
        fixClientApplication.onBeforeLogin(ctx, logonRequest);

        getLogger().info("Sending Logon: {}", logonRequest);

        ctx.writeAndFlush(logonRequest);
    }

    private FixSession createSession(FixSessionSettingsProvider settingsProvider) {

        int nextIncomingSeqNum;
        if (settingsProvider.isResetMsgSeqNum()) {
            nextIncomingSeqNum = 1;
        } else {
            nextIncomingSeqNum = messageSequenceProvider.getMsgInSeqNum();
        }

        FixSession session = FixSession.newBuilder()
                .beginString(settingsProvider.getBeginString())
                .senderCompId(settingsProvider.getSenderCompID())
                .senderSubId(settingsProvider.getSenderSubID())
                .targetCompId(settingsProvider.getTargetCompID())
                .targetSubId(settingsProvider.getTargetSubID())
                .nextIncomingSeqNum(nextIncomingSeqNum)
                .build();
        session.setNextOutgoingMessageSeqNum(messageSequenceProvider.getMsgOutSeqNum());
        return session;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}