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
package fixio.netty.codec;

import fixio.fixprotocol.FieldType;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.FixMessageImpl;
import fixio.fixprotocol.MessageTypes;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.*;

public class FixMessageDecoderTest {

    private static FixMessageDecoder decoder;

    @BeforeClass
    public static void setUp() throws Exception {
        decoder = new FixMessageDecoder();
    }

    @Test
    public void testDecode() throws Exception {
        List<Object> result = decode("8=FIX.4.1\u00019=90\u000135=0\u000149=INVMGR\u000156=BRKR\u000134=240\u000152=19980604-08:03:31\u000110=129\u0001");

        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof FixMessageImpl);
        final FixMessageImpl fixMessage = (FixMessageImpl) result.get(0);

        FixMessageHeader header = fixMessage.getHeader();

        assertEquals("FIX.4.1", fixMessage.getHeader().getBeginString());
        assertEquals(MessageTypes.HEARTBEAT, fixMessage.getMessageType());
        assertEquals("INVMGR", header.getSenderCompID());
        assertEquals("BRKR", header.getTargetCompID());
        assertEquals(240, header.getMsgSeqNum());
        assertEquals(129, fixMessage.getChecksum());

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR, 1998);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 4);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 3);
        calendar.set(Calendar.SECOND, 31);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(calendar.getTimeInMillis(), fixMessage.getValue(FieldType.SendingTime));
    }

    @Test
    public void testCmeRealData() throws Exception {
        List<Object> result = decode("8=FIX.4.29=39635=BZ34=148949=CME50=G52=20141210-04:12:58.68956=17ACPON57=DUMMY369=36701180=0K41181=42811350=428011=ACP141818477867860=20141210-04:12:58.686533=3797=Y893=Y1028=Y1300=991369=9971:21373=31374=91375=1453=2448=000447=D452=7448=US,IL447=D452=54534=341=ACP141818477617384=60535=99499752041=ACP141818477621484=60535=99499752141=ACP141818477625384=180535=99499752210=228");
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof FixMessageImpl);
        System.out.println(result.get(0));
    }

    private List<Object> decode(String message) throws Exception {
        String[] tags = message.split("\u0001");

        List<Object> result = new ArrayList<>();
        for (String tag : tags) {
            decoder.decode(null, Unpooled.wrappedBuffer(tag.getBytes(StandardCharsets.US_ASCII)), result);
        }
        return result;
    }

    @Test
    public void testNoBeginTag() throws Exception {
        String random=randomAlphanumeric(50);

        try {
            decode("100=" + random + "\u00018=FIX.4.2...");
            fail("DecoderException is expected");
        } catch (DecoderException e) {
            assertEquals("BeginString tag expected, but got: 100=" + random.substring(0,10) + "...", e.getMessage());
        }
    }
}
