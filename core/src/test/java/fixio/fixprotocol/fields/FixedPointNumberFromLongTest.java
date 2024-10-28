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

public class FixedPointNumberFromLongTest {

    private final byte expectedScale = 0;
    private FixedPointNumber value;

    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0L, "0"},
                {1234567890L, "1234567890"},
                {-1234567890L, "-1234567890"},
        });
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0,number}")
    void scaledValue(long longValue, String expectedToString) {
        value = new FixedPointNumber(longValue, expectedScale);
        assertThat(value.getScaledValue()).isEqualTo(longValue);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0,number}")
    void scale(long longValue, String expectedToString) {
        value = new FixedPointNumber(longValue, expectedScale);
        assertThat(value.getScale()).isEqualTo(expectedScale);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0,number}")
    strictfp void doubleValue(long longValue, String expectedToString) {
        value = new FixedPointNumber(longValue, expectedScale);
        assertThat(value.doubleValue()).isCloseTo(longValue, within(0.0));
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0,number}")
    void longValue(long longValue, String expectedToString) {
        value = new FixedPointNumber(longValue, expectedScale);
        assertThat(value.longValue()).isEqualTo(longValue);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0,number}")
    void testToString(long longValue, String expectedToString) {
        value = new FixedPointNumber(longValue, expectedScale);
        assertThat(value.toString()).isEqualTo(expectedToString);
    }
}
