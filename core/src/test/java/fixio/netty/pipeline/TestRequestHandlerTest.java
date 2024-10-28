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

import fixio.fixprotocol.FixMessageBuilderImpl;
import fixio.fixprotocol.MessageTypes;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static fixio.fixprotocol.FieldType.TestReqID;
import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestRequestHandlerTest {

    private TestRequestHandler handler;
    @Mock
    private FixMessageBuilderImpl fixMessage;
    @Mock
    private ChannelHandlerContext ctx;
    @Mock
    private Channel channel;
    @Captor
    private ArgumentCaptor<FixMessageBuilderImpl> messageBuilderCaptor;

    @BeforeEach
    void setUp() {
        handler = new TestRequestHandler();
    }

    @Test
    void rejectNotSupportedObject() {
        assertThat(handler.acceptInboundMessage(new Object())).isFalse();
    }

    @Test
    void rejectNullObject() {
        assertThat(handler.acceptInboundMessage(null)).isFalse();
    }

    @Test
    void acceptFixMessage() {
        when(fixMessage.getMessageType()).thenReturn(MessageTypes.TEST_REQUEST);
        assertThat(handler.acceptInboundMessage(fixMessage)).isTrue();
    }

    @Test
    void skipOtherMessage() throws Exception {
        when(fixMessage.getMessageType()).thenReturn(MessageTypes.HEARTBEAT);

        ArrayList<Object> out = new ArrayList<>();
        handler.decode(ctx, fixMessage, out);

        verifyNoInteractions(ctx);
        assertThat(out.isEmpty()).isTrue();
    }

    @Test
    void handleTestRequest() throws Exception {
        String testReqId = randomAscii(10);
        when(ctx.channel()).thenReturn(channel);
        when(fixMessage.getMessageType()).thenReturn(MessageTypes.TEST_REQUEST);
        when(fixMessage.getString(TestReqID)).thenReturn(testReqId);

        List<Object> result = new ArrayList<>();
        handler.decode(ctx, fixMessage, result);

        verify(channel, times(1)).writeAndFlush(messageBuilderCaptor.capture());

        final FixMessageBuilderImpl fixMessageBuilder = messageBuilderCaptor.getValue();

        assertThat(fixMessageBuilder.getMessageType()).isEqualTo(MessageTypes.HEARTBEAT);
        assertThat(testReqId).isEqualTo(fixMessageBuilder.getString(TestReqID));
    }
}
