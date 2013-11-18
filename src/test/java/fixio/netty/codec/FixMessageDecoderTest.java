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

import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.SimpleFixMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FixMessageDecoderTest {
    private FixMessageDecoder decoder;

    @Before
    public void setUp() throws Exception {
        decoder = new FixMessageDecoder();
    }

    @Test
    public void testDecode() throws Exception {
        String fixString = "8=FIX.4.1\u00019=90\u000135=0\u000149=INVMGR\u000156=BRKR\u000134=240\u000152=19980604-08:03:31\u000110=220\u0001";
        byte[] fixBytes = fixString.getBytes(Charset.forName("ASCII"));
        ByteBuf byteBuf = Unpooled.wrappedBuffer(fixBytes);

        List<Object> result = new ArrayList<>();
        decoder.decode(null, byteBuf, result);
        Assert.assertEquals(1, result.size());
        final SimpleFixMessage fixMessage = (SimpleFixMessage) result.get(0);

        Assert.assertEquals("FIX.4.1", fixMessage.getBeginString());
        Assert.assertEquals(MessageTypes.HEARTBEAT, fixMessage.getMessageType());
        Assert.assertEquals("INVMGR", fixMessage.getString(49));
        Assert.assertEquals("BRKR", fixMessage.getString(56));
        Assert.assertEquals(240, (long) fixMessage.getInt(34));
        Assert.assertEquals("19980604-08:03:31", fixMessage.getString(52));
        Assert.assertEquals(220, fixMessage.getChecksum());
    }
}
