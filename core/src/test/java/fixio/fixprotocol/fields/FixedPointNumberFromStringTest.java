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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FixedPointNumberFromStringTest {

    private final String string;
    private final int offset;
    private final int length;
    private final long expectedScaledValue;
    private final int expectedScale;
    private final String expectedToString;

    private FixedPointNumber value;

    public FixedPointNumberFromStringTest(String string, int offset, int length,
                                          long expectedScaledValue, int expectedScale, String expectedToString) {
        this.string = string;
        this.offset = offset;
        this.length = length;
        this.expectedScaledValue = expectedScaledValue;
        this.expectedScale = expectedScale;
        this.expectedToString = expectedToString;
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"abc1234567890def", 3, 10, 1234567890L, 0, "1234567890"},
                {"abc-1234567890def", 3, 11, -1234567890L, 0, "-1234567890"},
                {"abc1.234567890def", 3, 11, 1234567890L, 9, "1.234567890"},
                {"abc-1.234567890def", 3, 12, -1234567890L, 9, "-1.234567890"},
                {"abc+1.234567890def", 3, 12, 1234567890L, 9, "1.234567890"}
        });
    }

    @Before
    public void setUp() {
        value = new FixedPointNumber(string.getBytes(US_ASCII), offset, length);
    }

    @Test
    public void testScaledValue() {
        assertEquals(expectedScaledValue, value.getScaledValue());
    }

    @Test
    public void testScale() {
        assertEquals(expectedScale, value.getScale());
    }

    @Test
    public void testDoubleValue() {
        double expectedDouble = new BigDecimal(string.substring(offset, offset + length)).doubleValue();
        assertEquals(expectedDouble, value.doubleValue(), 0.0);
    }

    @Test
    public void testLongValue() {
        long expectedLong = new BigDecimal(string.substring(offset, offset + length)).longValue();
        assertEquals(expectedLong, value.longValue());
    }

    @Test
    public void testToString() {
        assertEquals(expectedToString, value.toString());
    }
}
