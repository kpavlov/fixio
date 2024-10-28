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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static fixio.fixprotocol.FixConst.DEFAULT_ZONE_ID;
import static fixio.netty.codec.DecodingTestHelper.decodeOne;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class FixMessageDecoderTest {

    private static FixMessageDecoder decoder;

    @BeforeAll
    static void setUp() {
        decoder = new FixMessageDecoder();
    }

    @Test
    void shouldDecode() {
        FixMessageImpl fixMessage = decodeOne("8=FIX.4.1\u00019=90\u000135=0\u000149=INVMGR\u000156=BRKR\u000134=240\u000152=19980604-08:03:31\u000110=129\u0001", decoder);

        FixMessageHeader header = fixMessage.getHeader();

        assertThat(fixMessage.getHeader().getBeginString()).isEqualTo("FIX.4.1");
        assertThat(fixMessage.getMessageType()).isEqualTo(MessageTypes.HEARTBEAT);
        assertThat(header.getSenderCompID()).isEqualTo("INVMGR");
        assertThat(header.getTargetCompID()).isEqualTo("BRKR");
        assertThat(header.getMsgSeqNum()).isEqualTo(240);
        assertThat(fixMessage.getChecksum()).isEqualTo(129);

        final ZonedDateTime value = fixMessage.getValue(FieldType.SendingTime);

        ZonedDateTime expected = ZonedDateTime.of(LocalDate.of(1998, 6, 4),
                LocalTime.of(8, 3, 31, 0), DEFAULT_ZONE_ID);
        assertThat(value).isEqualTo(expected);
    }

    @Test
    void noBeginTag() {
        String random = randomAlphanumeric(50);

        try {
            decode("100=" + random + "\u00018=FIX.4.2...");
            fail("DecoderException is expected");
        } catch (DecoderException e) {
            assertThat(e.getMessage()).isEqualTo("BeginString tag expected, but got: 100=" + random.substring(0, 10) + "...");
        }
    }

    private List<Object> decode(String message) {
        String[] tags = message.split("\u0001");

        List<Object> result = new ArrayList<>();
        for (String tag : tags) {
            decoder.decode(null, Unpooled.wrappedBuffer(tag.getBytes(StandardCharsets.US_ASCII)), result);
        }
        return result;
    }
}
