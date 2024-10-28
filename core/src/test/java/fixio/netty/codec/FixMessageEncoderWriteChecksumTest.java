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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class FixMessageEncoderWriteChecksumTest {

    private ByteBuf byteBuf;
    private int value;
    private byte[] expectedBytes;

    public void initFixMessageEncoderWriteChecksumTest(int value, byte[] expectedBytes) {
        this.byteBuf = Unpooled.buffer(7);
        this.value = value;
        this.expectedBytes = expectedBytes;
    }

    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, new byte[]{'1', '0', '=', '0', '0', '0', 1}},
                {1, new byte[]{'1', '0', '=', '0', '0', '1', 1}},
                {9, new byte[]{'1', '0', '=', '0', '0', '9', 1}},
                {10, new byte[]{'1', '0', '=', '0', '1', '0', 1}},
                {99, new byte[]{'1', '0', '=', '0', '9', '9', 1}},
                {100, new byte[]{'1', '0', '=', '1', '0', '0', 1}},
                {190, new byte[]{'1', '0', '=', '1', '9', '0', 1}},
                {999, new byte[]{'1', '0', '=', '9', '9', '9', 1}},
        });
    }

    @MethodSource("data")
    @ParameterizedTest(name = "Value of {0}")
    public void writeChecksum(int value, byte[] expectedBytes) {
        initFixMessageEncoderWriteChecksumTest(value, expectedBytes);
        FixMessageEncoder.writeChecksumField(byteBuf, value);
        assertArrayEquals(expectedBytes, byteBuf.array());
    }
}
