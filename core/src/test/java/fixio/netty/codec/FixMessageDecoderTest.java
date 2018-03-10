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
import io.netty.handler.codec.DecoderException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static fixio.fixprotocol.FixConst.DEFAULT_ZONE_ID;
import static fixio.netty.codec.DecodingTestHelper.decode;
import static fixio.netty.codec.DecodingTestHelper.decodeOne;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FixMessageDecoderTest {

    private static FixMessageDecoder decoder;

    @BeforeClass
    public static void setUp() {
        decoder = new FixMessageDecoder();
    }

    @Test
    public void testDecode() {
        FixMessageImpl fixMessage = decodeOne("8=FIX.4.1\u00019=90\u000135=0\u000149=INVMGR\u000156=BRKR\u000134=240\u000152=19980604-08:03:31\u000110=129\u0001", decoder);

        FixMessageHeader header = fixMessage.getHeader();

        assertEquals("FIX.4.1", fixMessage.getHeader().getBeginString());
        assertEquals(MessageTypes.HEARTBEAT, fixMessage.getMessageType());
        assertEquals("INVMGR", header.getSenderCompID());
        assertEquals("BRKR", header.getTargetCompID());
        assertEquals(240, header.getMsgSeqNum());
        assertEquals(129, fixMessage.getChecksum());

        final ZonedDateTime value = fixMessage.getValue(FieldType.SendingTime);

        ZonedDateTime expected = ZonedDateTime.of(LocalDate.of(1998, 6, 4),
                LocalTime.of(8, 3, 31, 0), DEFAULT_ZONE_ID);
        assertEquals(expected, value);
    }

    @Test
    public void testNoBeginTag() {
        String random = randomAlphanumeric(50);

        try {
            decode("100=" + random + "\u00018=FIX.4.2...", decoder);
            fail("DecoderException is expected");
        } catch (DecoderException e) {
            assertEquals("BeginString tag expected, but got: 100=" + random.substring(0, 10) + "...", e.getMessage());
        }
    }
}
