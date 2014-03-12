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

import fixio.fixprotocol.FieldType;
import fixio.fixprotocol.FixMessageBuilderImpl;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.session.FixSession;
import fixio.handlers.FixApplication;
import fixio.netty.AttributeMock;
import fixio.netty.pipeline.AbstractSessionHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ClientSessionHandlerTest {

    private ClientSessionHandler handler;
    @Mock
    private FixSessionSettingsProvider settingsProvider;
    @Mock
    private MessageSequenceProvider sequenceProvider;
    @Mock
    private ChannelHandlerContext ctx;
    @Mock
    private FixApplication fixApplication;
    @Captor
    private ArgumentCaptor<FixMessageBuilderImpl> messageCaptor;
    private int inMsgSeqNum;
    private int outMsgSeqNum;
    private Integer heartbeartInterval;

    @Before
    public void setUp() {
        handler = spy(new ClientSessionHandler(settingsProvider, sequenceProvider, fixApplication));

        Random random = new Random();
        inMsgSeqNum = random.nextInt();
        outMsgSeqNum = random.nextInt();

        heartbeartInterval = random.nextInt(100) + 10;
        when(settingsProvider.getHeartbeatInterval()).thenReturn(heartbeartInterval);
        when(settingsProvider.getSenderCompID()).thenReturn(randomAscii(5));
        when(settingsProvider.getTargetCompID()).thenReturn(randomAscii(5));

        when(sequenceProvider.getMsgInSeqNum()).thenReturn(inMsgSeqNum);
        when(sequenceProvider.getMsgOutSeqNum()).thenReturn(outMsgSeqNum);

        Attribute<FixSession> sessionAttribute = new AttributeMock<>();
        when(ctx.attr(AbstractSessionHandler.FIX_SESSION_KEY)).thenReturn(sessionAttribute);
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
}
