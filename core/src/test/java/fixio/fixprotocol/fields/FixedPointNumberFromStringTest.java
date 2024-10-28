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
package fixio.fixprotocol.fields;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class FixedPointNumberFromStringTest {

    private String string;
    private int offset;
    private int length;
    private long expectedScaledValue;
    private int expectedScale;
    private String expectedToString;

    private FixedPointNumber value;

    public void initFixedPointNumberFromStringTest(String string, int offset, int length,
                                          long expectedScaledValue, int expectedScale, String expectedToString) {
        this.string = string;
        this.offset = offset;
        this.length = length;
        this.expectedScaledValue = expectedScaledValue;
        this.expectedScale = expectedScale;
        this.expectedToString = expectedToString;
    }

    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"abc1234567890def", 3, 10, 1234567890L, 0, "1234567890"},
                {"abc-1234567890def", 3, 11, -1234567890L, 0, "-1234567890"},
                {"abc1.234567890def", 3, 11, 1234567890L, 9, "1.234567890"},
                {"abc-1.234567890def", 3, 12, -1234567890L, 9, "-1.234567890"},
                {"abc+1.234567890def", 3, 12, 1234567890L, 9, "1.234567890"}
        });
    }

    @BeforeEach
    void setUp() {
        value = new FixedPointNumber(string.getBytes(US_ASCII), offset, length);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void scaledValue(String string, int offset, int length, long expectedScaledValue, int expectedScale, String expectedToString) {
        initFixedPointNumberFromStringTest(string, offset, length, expectedScaledValue, expectedScale, expectedToString);
        assertThat(value.getScaledValue()).isEqualTo(expectedScaledValue);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void scale(String string, int offset, int length, long expectedScaledValue, int expectedScale, String expectedToString) {
        initFixedPointNumberFromStringTest(string, offset, length, expectedScaledValue, expectedScale, expectedToString);
        assertThat(value.getScale()).isEqualTo(expectedScale);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void doubleValue(String string, int offset, int length, long expectedScaledValue, int expectedScale, String expectedToString) {
        initFixedPointNumberFromStringTest(string, offset, length, expectedScaledValue, expectedScale, expectedToString);
        double expectedDouble = new BigDecimal(string.substring(offset, offset + length)).doubleValue();
        assertThat(value.doubleValue()).isCloseTo(expectedDouble, within(0.0));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void longValue(String string, int offset, int length, long expectedScaledValue, int expectedScale, String expectedToString) {
        initFixedPointNumberFromStringTest(string, offset, length, expectedScaledValue, expectedScale, expectedToString);
        long expectedLong = new BigDecimal(string.substring(offset, offset + length)).longValue();
        assertThat(value.longValue()).isEqualTo(expectedLong);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void testToString(String string, int offset, int length, long expectedScaledValue, int expectedScale, String expectedToString) {
        initFixedPointNumberFromStringTest(string, offset, length, expectedScaledValue, expectedScale, expectedToString);
        assertThat(value.toString()).isEqualTo(expectedToString);
    }
}
