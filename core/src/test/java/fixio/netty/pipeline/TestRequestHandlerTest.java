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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static fixio.fixprotocol.FieldType.TestReqID;
import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestRequestHandlerTest {

    private TestRequestHandler handler;
    @Mock
    private FixMessageBuilderImpl fixMessage;
    @Mock
    private ChannelHandlerContext ctx;
    @Mock
    private Channel channel;
    @Captor
    private ArgumentCaptor<FixMessageBuilderImpl> messageBuilderCaptor;

    @Before
    public void setUp() {
        handler = new TestRequestHandler();
    }

    @Test
    public void testRejectNotSupportedObject() {
        assertFalse(handler.acceptInboundMessage(new Object()));
    }

    @Test
    public void testRejectNullObject() {
        assertFalse(handler.acceptInboundMessage(null));
    }

    @Test
    public void testAcceptFixMessage() {
        when(fixMessage.getMessageType()).thenReturn(MessageTypes.TEST_REQUEST);
        assertTrue(handler.acceptInboundMessage(fixMessage));
    }

    @Test
    public void testSkipOtherMessage() throws Exception {
        when(fixMessage.getMessageType()).thenReturn(MessageTypes.HEARTBEAT);

        ArrayList<Object> out = new ArrayList<>();
        handler.decode(ctx, fixMessage, out);

        verifyZeroInteractions(ctx);
        assertTrue(out.isEmpty());
    }

    @Test
    public void testHandleTestRequest() throws Exception {
        String testReqId = randomAscii(10);
        when(ctx.channel()).thenReturn(channel);
        when(fixMessage.getMessageType()).thenReturn(MessageTypes.TEST_REQUEST);
        when(fixMessage.getString(TestReqID)).thenReturn(testReqId);

        List<Object> result = new ArrayList<>();
        handler.decode(ctx, fixMessage, result);

        verify(channel, times(1)).writeAndFlush(messageBuilderCaptor.capture());

        final FixMessageBuilderImpl fixMessageBuilder = messageBuilderCaptor.getValue();

        assertEquals(MessageTypes.HEARTBEAT, fixMessageBuilder.getMessageType());
        assertEquals(fixMessageBuilder.getString(TestReqID), testReqId);
    }
}
