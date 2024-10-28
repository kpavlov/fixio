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
import fixio.fixprotocol.session.SessionId;
import fixio.handlers.FixApplication;
import fixio.validator.BusinessRejectException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractSessionHandlerTest {

    private static final Random RANDOM = new Random();
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSessionHandlerTest.class);
    private AbstractSessionHandler sessionHandler;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private ChannelHandlerContext ctx;
    @Mock
    private Channel channel;
    @Mock
    private FixSession fixSession;
    @Mock
    private FixApplication fixApplication;
    @Mock
    private Attribute<FixSession> sessionAttr;
    @Captor
    private ArgumentCaptor<FixMessage> rejectCaptor;
    @Mock
    private SessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        when(ctx.channel()).thenReturn(channel);
        sessionHandler = new AbstractSessionHandler(fixApplication, FixClock.systemUTC(), sessionRepository) {
            @Override
            protected void encode(ChannelHandlerContext ctx, FixMessageBuilder msg, List<Object> out) {
            }

            @Override
            protected void decode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) {
            }

            @Override
            protected Logger getLogger() {
                return LOGGER;
            }
        };
    }

    @Test
    void sendReject() {
        String msgType = randomAscii(3);
        FixMessage originalMsg = mock(FixMessage.class);
        int originalMsgSeqNum = RANDOM.nextInt();
        when(originalMsg.getMessageType()).thenReturn(msgType);
        when(originalMsg.getInt(FieldType.MsgSeqNum.tag())).thenReturn(originalMsgSeqNum);

        sessionHandler.sendReject(ctx, originalMsg, false);

        verify(ctx, times(1)).writeAndFlush(rejectCaptor.capture());

        FixMessage reject = rejectCaptor.getValue();

        assertThat(reject.getMessageType()).isEqualTo(MessageTypes.REJECT);
        assertThat(reject.getString(FieldType.RefMsgType.tag())).isEqualTo(msgType);
        assertThat(reject.getInt(FieldType.RefSeqNum.tag())).isEqualTo((Integer) originalMsgSeqNum);
    }

    @Test
    void channelInactiveSessionExists() throws Exception {
        when(channel.attr(AbstractSessionHandler.FIX_SESSION_KEY)).thenReturn(sessionAttr);
        when(sessionAttr.getAndSet(null)).thenReturn(fixSession);
        SessionId sessionId = mock(SessionId.class);
        when(fixSession.getId()).thenReturn(sessionId);

        sessionHandler.channelInactive(ctx);

        verify(ctx).fireChannelRead(any(LogoutEvent.class));
        verify(sessionAttr).getAndSet(null);
        verify(sessionRepository).removeSession(sessionId);
    }

    @Test
    void channelInactiveNoKeyAttr() throws Exception {
        when(channel.attr(AbstractSessionHandler.FIX_SESSION_KEY)).thenReturn(null);

        sessionHandler.channelInactive(ctx);

        verify(ctx, never()).fireChannelRead(any(LogoutEvent.class));
    }

    @Test
    void channelInactiveSessionNotExists() throws Exception {
        when(channel.attr(AbstractSessionHandler.FIX_SESSION_KEY)).thenReturn(sessionAttr);
        when(sessionAttr.getAndSet(null)).thenReturn(null);

        sessionHandler.channelInactive(ctx);

        verify(ctx, never()).fireChannelRead(any(LogoutEvent.class));
    }

    @Test
    void handleBusinessRejectException() throws Exception {
        int refSeqNum = RANDOM.nextInt(100) + 1;
        String refMsgType = randomAscii(2);
        int rejectReason = RANDOM.nextInt(5) + 1;
        String rejectText = randomAscii(10);
        BusinessRejectException exception = new BusinessRejectException(refSeqNum, refMsgType, rejectReason, rejectText);

        when(ctx.channel()).thenReturn(channel);

        sessionHandler.exceptionCaught(ctx, exception);

        verify(channel, times(1)).writeAndFlush(rejectCaptor.capture());

        FixMessage businessReject = rejectCaptor.getValue();

        assertThat(businessReject.getMessageType()).isEqualTo(MessageTypes.BUSINESS_MESSAGE_REJECT);
        assertThat(businessReject.getInt(FieldType.BusinessRejectReason).intValue()).isEqualTo(rejectReason);
        assertThat(businessReject.getInt(FieldType.RefSeqNum).intValue()).isEqualTo(refSeqNum);
        assertThat(businessReject.getString(FieldType.RefMsgType)).isEqualTo(refMsgType);
        assertThat(businessReject.getString(FieldType.Text)).isEqualTo(rejectText);
    }
}
