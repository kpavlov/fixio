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
package fixio.netty.pipeline;

import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.SimpleFixMessage;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static fixio.fixprotocol.FieldType.TestReqID;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestRequestHandlerTest {

    private TestRequestHandler handler;

    @Mock
    private SimpleFixMessage fixMessage;
    @Mock
    private ChannelHandlerContext ctx;
    @Captor
    private ArgumentCaptor<SimpleFixMessage> messageBuilderCaptor;

    @Before
    public void setUp() throws Exception {
        handler = new TestRequestHandler();
    }

    @Test
    public void testRejectNotSupportedObject() {
        assertFalse(handler.acceptInboundMessage(new Object()));
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

        handler.decode(ctx, fixMessage, null);

        verifyZeroInteractions(ctx);
    }

    @Test
    public void testHandleTestRequest() throws Exception {
        String testReqId = randomAlphanumeric(10);
        when(fixMessage.getMessageType()).thenReturn(MessageTypes.TEST_REQUEST);
        when(fixMessage.getString(TestReqID.tag())).thenReturn(testReqId);

        List<Object> result = new ArrayList<>();
        handler.decode(ctx, fixMessage, result);

        verify(ctx, times(1)).writeAndFlush(messageBuilderCaptor.capture());

        final SimpleFixMessage fixMessageBuilder = messageBuilderCaptor.getValue();

        assertEquals(MessageTypes.HEARTBEAT, fixMessageBuilder.getMessageType());
        assertEquals(fixMessageBuilder.getString(TestReqID.tag()), testReqId);
    }
}
