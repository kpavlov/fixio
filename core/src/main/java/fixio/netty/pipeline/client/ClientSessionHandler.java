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
import fixio.fixprotocol.fields.FieldFactory;
import fixio.fixprotocol.session.FixSession;
import fixio.handlers.FixApplication;
import fixio.netty.pipeline.AbstractSessionHandler;
import fixio.netty.pipeline.FixClock;
import fixio.netty.pipeline.InMemorySessionRepository;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.PasswordAuthentication;
import java.util.List;

import static fixio.fixprotocol.FieldType.DefaultApplExtID;
import static fixio.fixprotocol.FieldType.DefaultApplVerID;

public class ClientSessionHandler extends AbstractSessionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClientSessionHandler.class);
    private final FixSessionSettingsProvider sessionSettingsProvider;
    private final MessageSequenceProvider messageSequenceProvider;
    private final AuthenticationProvider authenticationProvider;

    protected ClientSessionHandler(FixSessionSettingsProvider settingsProvider,
                                AuthenticationProvider authenticationProvider,
                                MessageSequenceProvider messageSequenceProvider,
                                FixApplication fixApplication) {
        super(fixApplication, FixClock.systemUTC(), new InMemorySessionRepository());
        this.authenticationProvider = authenticationProvider;
        assert (settingsProvider != null) : "FixSessionSettingsProvider is expected.";
        this.sessionSettingsProvider = settingsProvider;
        this.messageSequenceProvider = messageSequenceProvider;
    }

    private FixMessageBuilderImpl createLogonRequest(FixSessionSettingsProvider sessionSettingsProvider) {
        FixMessageBuilderImpl messageBuilder = new FixMessageBuilderImpl(MessageTypes.LOGON);
        messageBuilder.add(FieldType.HeartBtInt, sessionSettingsProvider.getHeartbeatInterval());
        messageBuilder.add(FieldType.EncryptMethod, 0);
        if (authenticationProvider != null) {
            final PasswordAuthentication authentication = authenticationProvider.getPasswordAuthentication();
            if (authentication != null) {
                messageBuilder.add(FieldType.Username, authentication.getUserName());
                messageBuilder.add(FieldType.Password, String.valueOf(authentication.getPassword()));
            }
        }
        return messageBuilder;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        final FixMessageHeader header = msg.getHeader();
        FixSession session = getSession(ctx);
        if (MessageTypes.LOGON.equals(header.getMessageType())) {
            if (session != null) {
                int incomingMsgSeqNum = header.getMsgSeqNum();
                if (!session.checkAndIncrementIncomingSeqNum(incomingMsgSeqNum)) {
                    int expectedMsgSeqNum = session.getNextIncomingMessageSeqNum();
                    if (incomingMsgSeqNum > expectedMsgSeqNum) {
                        FixMessageBuilder resendRequest = new FixMessageBuilderImpl(MessageTypes.RESEND_REQUEST);
                        resendRequest.add(FieldType.BeginSeqNo, expectedMsgSeqNum);
                        resendRequest.add(FieldType.EndSeqNo, incomingMsgSeqNum - 1);
                        prepareMessageToSend(ctx, session, resendRequest);
                        ctx.writeAndFlush(resendRequest);
                    } else {
                        getLogger().warn("Message Sequence Too Low");
                        ctx.channel().close();
                        return;
                    }
                }
                getLogger().info("Fix Session Established.");
                LogonEvent logonEvent = new LogonEvent(session);
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

        FixMessageBuilder logonRequest = createLogonRequest(sessionSettingsProvider);

        FixSession pendingSession = createSession(sessionSettingsProvider);
        setSession(ctx, pendingSession);
        prepareMessageToSend(ctx, pendingSession, logonRequest);
        getLogger().info("Sending Logon: {}", logonRequest);

        ctx.writeAndFlush(logonRequest);
    }

    private FixSession createSession(FixSessionSettingsProvider settingsProvider) {
        String defaultApplVerID = ("FIXT.1.1".equalsIgnoreCase(settingsProvider.getBeginString()))?
                settingsProvider.getProperty("DefaultApplVerID", "7").trim(): null;
        //
        final FixSession session = FixSession.newBuilder()
                .beginString(settingsProvider.getBeginString())
                .senderCompId(settingsProvider.getSenderCompID())
                .senderSubId(settingsProvider.getSenderSubID())
                .senderLocationID(settingsProvider.getSenderLocationID())
                .targetCompId(settingsProvider.getTargetCompID())
                .targetSubId(settingsProvider.getTargetSubID())
                .targetLocationID(settingsProvider.getTargetLocationID())
                .timeStampPrecision(settingsProvider.getTimeStampPrecision())
                .defaultApplVerID(defaultApplVerID)
                .defaultApplExtID(settingsProvider.getProperty("DefaultApplExtID", null))
                .build();

        session.setNextOutgoingMessageSeqNum(messageSequenceProvider.getMsgOutSeqNum());
        session.setNextIncomingMessageSeqNum(settingsProvider.isResetMsgSeqNum() ? 1 : messageSequenceProvider.getMsgInSeqNum());
        return session;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
