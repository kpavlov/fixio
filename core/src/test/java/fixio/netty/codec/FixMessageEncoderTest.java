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
package fixio.netty.codec;

import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.SimpleFixMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FixMessageEncoderTest {

    private FixMessageEncoder encoder;
    private FixMessage builder;
    @Mock
    private ChannelHandlerContext ctx;

    @Before
    public void setUp() throws Exception {
        encoder = new FixMessageEncoder();

        SimpleFixMessage fixMessage = new SimpleFixMessage();

        FixMessageHeader header = fixMessage.getHeader();

        header.setBeginString(SimpleFixMessage.FIX_4_2);
        header.setMessageType(MessageTypes.HEARTBEAT);
        header.setSenderCompID("SenderCompID");
        header.setTargetCompID("TargetCompID");
        header.setMsgSeqNum(2);

        fixMessage.add(1001, "test2");
        fixMessage.add(1000, "test1");

        builder = fixMessage;
    }

    @Test
    public void testEncode() throws Exception {
        final ByteBuf out = Unpooled.buffer();

        encoder.encode(ctx, builder, out);

        verify(ctx).flush();

        final String string = new String(out.array(), out.arrayOffset(), out.readableBytes(), Charset.forName("ISO-8859-1"));
        assertTrue(out.array().length > 0);
        assertTrue(string.length() > 0);

        String expectedString = "8=FIX.4.2\u00019=64\u000135=0\u000149=SenderCompID\u000156=TargetCompID\u000134=2\u00011001=test2\u00011000=test1\u000110=227\u0001";
        assertEquals(expectedString, string);
    }
}
