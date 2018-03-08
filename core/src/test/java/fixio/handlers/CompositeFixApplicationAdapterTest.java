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
package fixio.handlers;

import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import fixio.fixprotocol.FixMessageBuilderImpl;
import fixio.fixprotocol.FixMessageImpl;
import fixio.validator.FixMessageValidator;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.when;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class CompositeFixApplicationAdapterTest {

    private CompositeFixApplicationAdapter adapter;
    @Mock
    private FixMessageValidator messageValidator1;
    @Mock
    private FixMessageValidator messageValidator2;
    @Mock
    private FixMessageHandler messageHandler1;
    @Mock
    private FixMessageHandler messageHandler2;
    @Mock
    private FixMessageHandler messageHandler3;
    @Mock
    private ChannelHandlerContext ctx;
    private ArrayList<Object> out;

    @Before
    public void setUp() {
        adapter = new CompositeFixApplicationAdapter(
                Arrays.asList(messageValidator1, messageValidator2),
                Arrays.asList(messageHandler1, messageHandler2, messageHandler3));
        out = new ArrayList<>();
    }

    @Test
    public void testOnMessage() throws Exception {
        final FixMessage message = new FixMessageImpl();

        when(messageHandler1.handle(same(ctx), same(message))).thenReturn(true);
        when(messageHandler2.handle(same(ctx), same(message))).thenReturn(false);

        adapter.onMessage(ctx, message, out);

        InOrder inOrder = inOrder(
                messageValidator1, messageValidator2,
                messageHandler1, messageHandler2, messageHandler3,
                ctx);

        inOrder.verify(messageValidator1).validate(same(ctx), same(message));
        inOrder.verify(messageValidator2).validate(same(ctx), same(message));

        inOrder.verify(messageHandler1).handle(same(ctx), same(message));
        inOrder.verify(messageHandler2).handle(same(ctx), same(message));
    }

    @Test
    public void testBeforeSendMessage() {
        final FixMessageBuilder message = new FixMessageBuilderImpl();


        adapter.beforeSendMessage(ctx, message);

        InOrder inOrder = inOrder(
                messageValidator1, messageValidator2,
                messageHandler1, messageHandler2, messageHandler3,
                ctx);

        inOrder.verify(messageHandler1).beforeSendMessage(same(ctx), same(message));
        inOrder.verify(messageHandler2).beforeSendMessage(same(ctx), same(message));
        inOrder.verify(messageHandler3).beforeSendMessage(same(ctx), same(message));
    }
}
