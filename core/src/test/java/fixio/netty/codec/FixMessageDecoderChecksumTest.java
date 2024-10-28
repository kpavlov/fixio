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


import fixio.fixprotocol.FixMessageImpl;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FixMessageDecoderChecksumTest {

    private static FixMessageDecoder decoder;
    private boolean checksumValid;
    private String fixMessage;
    private int expectedChecksum;

    public void initFixMessageDecoderChecksumTest(String fixMessage, int expectedChecksum, boolean checksumValid) {
        this.fixMessage = fixMessage;
        this.expectedChecksum = expectedChecksum;
        this.checksumValid = checksumValid;
    }

    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"8=FIX.4.4|9=165|35=X|34=2|49=SERVER|52=20130724-09:55:21.707|56=local.1021|57=Quote|268=0|10=210|", 210, true},
                {"8=FIX.4.2|9=178|35=8|49=PHLX|56=PERS|52=20071123-05:30:00.000|11=ATOMNOCCC9990900|20=3|150=E|39=E|55=MSFT|167=CS|54=1|38=15|40=2|44=15|58=PHLX EQUITY TESTING|59=0|47=C|32=0|31=0|151=15|14=0|6=0|10=128|", 128, true},
                {"8=FIX.4.2|9=65|35=A|49=SERVER|56=CLIENT|34=177|52=20090107-18:15:16|98=0|108=30|10=062|", 62, true},
                {"8=FIX.4.2|9=65|35=A|49=SERVER|56=CLIENT|34=177|52=20090107-18:15:16|98=0|108=30|10=060|", 62, false},
        });
    }

    @BeforeAll
    static void beforeClass() {
        decoder = new FixMessageDecoder();
    }

    private static List<Object> decode(String message) throws Exception {
        String[] tags = message.split("\\|");

        List<Object> result = new ArrayList<>();
        for (String tag : tags) {
            decoder.decode(null, Unpooled.wrappedBuffer(tag.getBytes(StandardCharsets.US_ASCII)), result);
        }
        return result;
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}. {1} ChecksumValid: {2}")
    void checksum(String fixMessage, int expectedChecksum, boolean checksumValid) throws Exception {
        initFixMessageDecoderChecksumTest(fixMessage, expectedChecksum, checksumValid);
        try {
            List<Object> result = decode(fixMessage);
            assertThat(checksumValid).as("Checksum is not valid, exception expected").isTrue();
            final FixMessageImpl msg = (FixMessageImpl) result.get(0);
            assertThat(msg.getChecksum()).isEqualTo(expectedChecksum);
        } catch (DecoderException e) {
            assertThat(checksumValid).as("Checksum is valid, no exception expected, but got: " + e).isFalse();
        }
    }

}
