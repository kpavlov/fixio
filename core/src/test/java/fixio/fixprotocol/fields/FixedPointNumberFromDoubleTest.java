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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class FixedPointNumberFromDoubleTest {

    private FixedPointNumber value;

    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {123456789.0, 123456789L, 0, "123456789"},
                {-123456789.0, -123456789L, 0, "-123456789"},
                {12345678.9, 123456789L, 1, "12345678.9"},
                {-12345678.9, -123456789L, 1, "-12345678.9"},
                {1.23456789, 123456789L, 8, "1.23456789"},
                {-1.23456789, -123456789L, 8, "-1.23456789"},
                {0.123456789, 123456789L, 9, "0.123456789"},
                {-0.123456789, -123456789L, 9, "-0.123456789"},
        });
    }


    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0,number}")
    void scaledValue(double source, long expectedScaledValue, int expectedScale, String expectedToString) {
        value = new FixedPointNumber(source, expectedScale);
        assertThat(value.getScaledValue()).isEqualTo(expectedScaledValue);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0,number}")
    void scale(double source, long expectedScaledValue, int expectedScale, String expectedToString) {
        value = new FixedPointNumber(source, expectedScale);
        assertThat(value.getScale()).isEqualTo((byte) expectedScale);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0,number}")
    void doubleValue(double source, long expectedScaledValue, int expectedScale, String expectedToString) {
        value = new FixedPointNumber(source, expectedScale);
        assertThat(value.doubleValue()).isCloseTo(source, within(0.0));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0,number}")
    void longValue(double source, long expectedScaledValue, int expectedScale, String expectedToString) {
        value = new FixedPointNumber(source, expectedScale);
        assertThat(value.longValue()).isEqualTo((long) source);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0,number}")
    void testToString(double source, long expectedScaledValue, int expectedScale, String expectedToString) {
        value = new FixedPointNumber(source, expectedScale);
        assertThat(value.toString()).isEqualTo(expectedToString);
    }
}
