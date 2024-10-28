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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FixMessageEncoderChecksumTest {

    private ByteBuf byteBuf;
    private int offset;
    private int expectedChecksum;

    public void initFixMessageEncoderChecksumTest(String str, int offset, int expectedChecksum) {
        this.byteBuf = Unpooled.wrappedBuffer(str.replaceAll("\\|", "\u0001").getBytes(StandardCharsets.US_ASCII));
        this.offset = offset;
        this.expectedChecksum = expectedChecksum;
    }

    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"8=FIX.4.2|9=178|35=8|49=PHLX|56=PERS|52=20071123-05:30:00.000|11=ATOMNOCCC9990900|20=3|150=E|39=E|55=MSFT|167=CS|54=1|38=15|40=2|44=15|58=PHLX EQUITY TESTING|59=0|47=C|32=0|31=0|151=15|14=0|6=0|", 0, 128},
                {"fooBar8=FIX.4.2|9=178|35=8|49=PHLX|56=PERS|52=20071123-05:30:00.000|11=ATOMNOCCC9990900|20=3|150=E|39=E|55=MSFT|167=CS|54=1|38=15|40=2|44=15|58=PHLX EQUITY TESTING|59=0|47=C|32=0|31=0|151=15|14=0|6=0|", 6, 128},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    public void calculateChecksum(String str, int offset, int expectedChecksum) {
        initFixMessageEncoderChecksumTest(str, offset, expectedChecksum);
        assertEquals(expectedChecksum, FixMessageEncoder.calculateChecksum(byteBuf, offset));
    }
}
