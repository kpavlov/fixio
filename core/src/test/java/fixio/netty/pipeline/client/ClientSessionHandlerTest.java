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
import fixio.fixprotocol.FieldType;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilderImpl;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.session.FixSession;
import fixio.handlers.FixApplication;
import fixio.netty.AttributeMock;
import fixio.netty.pipeline.AbstractSessionHandler;
import fixio.netty.pipeline.FixMessageAsserts;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class ClientSessionHandlerTest {

    private static final Random RANDOM = new Random();

    private ClientSessionHandler handler;
    @Mock
    private MessageSequenceProvider sequenceProvider;
    @Mock
    private ChannelHandlerContext ctx;
    @Mock
    private Channel channel;
    @Mock
    private FixApplication fixApplication;
    @Mock
    private AuthenticationProvider authenticationProvider;
    @Captor
    private ArgumentCaptor<FixMessageBuilderImpl> messageCaptor;
    private int outMsgSeqNum;
    private Integer heartbeartInterval;
    private Attribute<FixSession> sessionAttribute;
    private String userName;
    private String password;

    @Before
    public void setUp() {
        outMsgSeqNum = RANDOM.nextInt();
        userName = randomAscii(10);
        password = randomAscii(9);

        heartbeartInterval = RANDOM.nextInt(100) + 10;

        FixSessionSettingsProvider settingsProvider = FixSessionSettingsProviderImpl.newBuilder()
                .resetMsgSeqNum(true)
                .heartbeatInterval(heartbeartInterval)
                .beginString(randomAscii(5))
                .senderCompID(randomAscii(5))
                .targetCompID(randomAscii(5))
                .build();

        when(authenticationProvider.getPasswordAuthentication()).thenReturn(
                new PasswordAuthentication(userName, password.toCharArray())
        );

        handler = spy(new ClientSessionHandler(settingsProvider, authenticationProvider, sequenceProvider, fixApplication));

        when(sequenceProvider.getMsgOutSeqNum()).thenReturn(outMsgSeqNum);

        sessionAttribute = new AttributeMock<>();
        when(channel.attr(AbstractSessionHandler.FIX_SESSION_KEY)).thenReturn(sessionAttribute);
        when(ctx.channel()).thenReturn(channel);
    }

    @Test
    public void testChannelActiveNoAuthentication() throws Exception {
        when(authenticationProvider.getPasswordAuthentication()).thenReturn(null);

        handler.channelActive(ctx);

        verify(ctx).writeAndFlush(messageCaptor.capture());

        FixMessageBuilderImpl messageBuilder = messageCaptor.getValue();
        FixMessageHeader header = messageBuilder.getHeader();

        verify(fixApplication).beforeSendMessage(same(ctx), same(messageBuilder));

        assertEquals("LOGON expected", MessageTypes.LOGON, header.getMessageType());
        assertEquals(outMsgSeqNum, header.getMsgSeqNum());
        assertTrue(header.getSendingTime().toInstant().toEpochMilli() > 0);

        assertThat("HeartBtInt", messageBuilder.getInt(FieldType.HeartBtInt), is(heartbeartInterval));
        assertThat("EncryptMethod", messageBuilder.getInt(FieldType.EncryptMethod), is(0));
        assertThat("Username", messageBuilder.getString(FieldType.Username), nullValue());
        assertThat("Password", messageBuilder.getString(FieldType.Password), nullValue());
    }

    @Test
    public void testChannelActiveWithAuthentication() throws Exception {

        handler.channelActive(ctx);

        verify(ctx).writeAndFlush(messageCaptor.capture());

        FixMessageBuilderImpl messageBuilder = messageCaptor.getValue();
        FixMessageHeader header = messageBuilder.getHeader();

        verify(fixApplication).beforeSendMessage(same(ctx), same(messageBuilder));

        assertEquals("LOGON expected", MessageTypes.LOGON, header.getMessageType());
        assertEquals(outMsgSeqNum, header.getMsgSeqNum());
        assertTrue(header.getSendingTime().toInstant().toEpochMilli() > 0);

        assertThat("HeartBtInt", messageBuilder.getInt(FieldType.HeartBtInt), is(heartbeartInterval));
        assertThat("EncryptMethod", messageBuilder.getInt(FieldType.EncryptMethod), is(0));
        assertThat("Username", messageBuilder.getString(FieldType.Username), is(userName));
        assertThat("Password", messageBuilder.getString(FieldType.Password), is(password));
    }

    @Test
    public void testSequenceTooHigh() throws Exception {
        FixMessage logonResponseMsg = new FixMessageBuilderImpl(MessageTypes.LOGON);
        FixMessageHeader header = logonResponseMsg.getHeader();
        header.setMsgSeqNum(3);
        header.setSenderCompID(randomAscii(3));
        header.setTargetCompID(randomAscii(4));

        logonResponseMsg.getHeader().setMsgSeqNum(3);

        FixSession fixSession = FixSession.newBuilder()
                .senderCompID(header.getSenderCompID())
                .targetCompID(header.getTargetCompID())
                .build();
        fixSession.setNextIncomingMessageSeqNum(1);

        sessionAttribute.set(fixSession);

        final List<Object> outgoingMessages = new ArrayList<>();

        // emulate logon response from server
        handler.decode(ctx, logonResponseMsg, outgoingMessages);

        assertEquals(1, outgoingMessages.size());
        assertTrue(outgoingMessages.get(0) instanceof LogonEvent);

        verify(ctx).writeAndFlush(messageCaptor.capture());

        final FixMessageBuilderImpl sentMessage = messageCaptor.getValue();
        FixMessageAsserts.assertResendRequest(sentMessage, 1, 2);
    }

    @Test
    public void testSequenceTooLow() throws Exception {
        FixMessage logonResponseMsg = new FixMessageBuilderImpl(MessageTypes.LOGON);
        FixMessageHeader header = logonResponseMsg.getHeader();
        header.setMsgSeqNum(3);
        header.setSenderCompID(randomAscii(3));
        header.setTargetCompID(randomAscii(4));

        logonResponseMsg.getHeader().setMsgSeqNum(3);

        FixSession fixSession = FixSession.newBuilder()
                .senderCompID(header.getSenderCompID())
                .targetCompID(header.getTargetCompID())
                .build();
        fixSession.setNextIncomingMessageSeqNum(4);

        sessionAttribute.set(fixSession);

        final List<Object> outgoingMessages = new ArrayList<>();

        // emulate logon response from server
        handler.decode(ctx, logonResponseMsg, outgoingMessages);

        assertEquals(0, outgoingMessages.size());

        verify(channel).close();
    }

}
