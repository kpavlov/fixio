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
package fixio.netty.pipeline;

import fixio.events.LogoutEvent;
import fixio.fixprotocol.FieldType;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.session.FixSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AbstractSessionHandlerTest {

    public static final Random RANDOM = new Random();
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSessionHandlerTest.class);
    private AbstractSessionHandler sessionHandler;
    @Mock
    private ChannelHandlerContext ctx;
    @Mock
    private FixSession fixSession;
    @Mock
    private Attribute<FixSession> sessonAttr;
    @Captor
    private ArgumentCaptor<FixMessage> rejectCaptor;

    @Before
    public void setUp() {
        sessionHandler = new AbstractSessionHandler() {
            @Override
            protected void encode(ChannelHandlerContext ctx, FixMessageBuilder msg, List<Object> out) throws Exception {
            }

            @Override
            protected void decode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
            }

            @Override
            protected Logger getLogger() {
                return LOGGER;
            }
        };
    }

    @Test
    public void testSendReject() throws Exception {
        String msgType = randomAscii(3);
        FixMessage originalMsg = mock(FixMessage.class);
        int originalMsgSeqNum = RANDOM.nextInt();
        when(originalMsg.getMessageType()).thenReturn(msgType);
        when(originalMsg.getInt(FieldType.MsgSeqNum.tag())).thenReturn(originalMsgSeqNum);

        sessionHandler.sendReject(ctx, originalMsg, false);

        verify(ctx, times(1)).writeAndFlush(rejectCaptor.capture());

        FixMessage reject = rejectCaptor.getValue();

        assertEquals(MessageTypes.REJECT, reject.getMessageType());
        assertEquals(msgType, reject.getString(FieldType.RefMsgType.tag()));
        assertEquals((Integer) originalMsgSeqNum, reject.getInt(FieldType.RefSeqNum.tag()));
    }

    @Test
    public void testChannelInactiveSessionExists() throws Exception {
        when(ctx.attr(AbstractSessionHandler.FIX_SESSION_KEY)).thenReturn(sessonAttr);
        when(sessonAttr.getAndRemove()).thenReturn(fixSession);

        sessionHandler.channelInactive(ctx);

        verify(ctx).fireChannelRead(any(LogoutEvent.class));
        verify(sessonAttr).getAndRemove();
    }

    @Test
    public void testChannelInactiveNoKeyAttr() throws Exception {
        when(ctx.attr(AbstractSessionHandler.FIX_SESSION_KEY)).thenReturn(null);

        sessionHandler.channelInactive(ctx);

        verify(ctx, never()).fireChannelRead(any(LogoutEvent.class));
    }

    @Test
    public void testChannelInactiveSessionNotExists() throws Exception {
        when(ctx.attr(AbstractSessionHandler.FIX_SESSION_KEY)).thenReturn(sessonAttr);
        when(sessonAttr.getAndRemove()).thenReturn(null);

        sessionHandler.channelInactive(ctx);

        verify(ctx, never()).fireChannelRead(any(LogoutEvent.class));
    }
}