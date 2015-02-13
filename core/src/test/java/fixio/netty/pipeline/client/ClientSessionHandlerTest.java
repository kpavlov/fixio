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
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
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
    @Captor
    private ArgumentCaptor<FixMessageBuilderImpl> messageCaptor;
    private int inMsgSeqNum;
    private int outMsgSeqNum;
    private Integer heartbeartInterval;
    private Attribute<FixSession> sessionAttribute;

    @Before
    public void setUp() {
        inMsgSeqNum = RANDOM.nextInt();
        outMsgSeqNum = RANDOM.nextInt();

        heartbeartInterval = RANDOM.nextInt(100) + 10;

        SimpleFixSessionSettingsProvider settingsProvider = new SimpleFixSessionSettingsProvider();
        settingsProvider.setResetMsgSeqNum(true);
        settingsProvider.setHeartbeatInterval(heartbeartInterval);
        settingsProvider.setBeginString(randomAscii(5));
        settingsProvider.setSenderCompID(randomAscii(5));
        settingsProvider.setTargetCompID(randomAscii(5));

        handler = spy(new ClientSessionHandler(settingsProvider, sequenceProvider, fixApplication));

        when(sequenceProvider.getMsgInSeqNum()).thenReturn(inMsgSeqNum);
        when(sequenceProvider.getMsgOutSeqNum()).thenReturn(outMsgSeqNum);

        sessionAttribute = new AttributeMock<>();
        when(ctx.attr(AbstractSessionHandler.FIX_SESSION_KEY)).thenReturn(sessionAttribute);
        when(ctx.channel()).thenReturn(channel);
    }

    @Test
    public void testChannelActive() throws Exception {

        handler.channelActive(ctx);

        verify(ctx).writeAndFlush(messageCaptor.capture());

        FixMessageBuilderImpl messageBuilder = messageCaptor.getValue();
        FixMessageHeader header = messageBuilder.getHeader();

        verify(fixApplication).beforeSendMessage(same(ctx), same(messageBuilder));

        assertEquals("LOGON expected", MessageTypes.LOGON, header.getMessageType());
        assertEquals(outMsgSeqNum, header.getMsgSeqNum());
        assertTrue(header.getSendingTime() > 0);

        assertEquals("HeartBtInt", heartbeartInterval, messageBuilder.getInt(FieldType.HeartBtInt));
        assertEquals("EncryptMethod", (Integer) 0, messageBuilder.getInt(FieldType.EncryptMethod));
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
                .senderCompId(header.getSenderCompID())
                .targetCompId(header.getTargetCompID())
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
                .senderCompId(header.getSenderCompID())
                .targetCompId(header.getTargetCompID())
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
