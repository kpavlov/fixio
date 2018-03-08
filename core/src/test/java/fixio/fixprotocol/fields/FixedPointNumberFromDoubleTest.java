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

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FixedPointNumberFromDoubleTest {

    private final double source;
    private final long expectedScaledValue;
    private final int expectedScale;
    private final String expectedToString;
    private FixedPointNumber value;

    public FixedPointNumberFromDoubleTest(double d, long expectedScaledValue, int expectedScale, String expectedToString) {
        this.source = d;
        this.expectedScaledValue = expectedScaledValue;
        this.expectedScale = expectedScale;
        this.expectedToString = expectedToString;
    }

    @Parameterized.Parameters(name = "{index}: {0,number}")
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

    @Before
    public void setUp() {
        value = new FixedPointNumber(source, expectedScale);
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
    public strictfp void testDoubleValue() {
        assertEquals(source, value.doubleValue(), 0.0);
    }

    @Test
    public void testLongValue() {
        assertEquals((long) source, value.longValue());
    }

    @Test
    public void testToString() {
        assertEquals(expectedToString, value.toString());
    }
}
