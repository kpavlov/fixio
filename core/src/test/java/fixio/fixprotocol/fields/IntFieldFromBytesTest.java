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
import java.util.Random;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class IntFieldFromBytesTest {

    private final String string;
    private final int offset;
    private final int length;
    private final int expectedValue;
    private IntField intField;
    private int tagNum;

    public IntFieldFromBytesTest(String string, int offset, int length, int expectedValue) {
        this.string = string;
        this.offset = offset;
        this.length = length;
        this.expectedValue = expectedValue;
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"1234567890", 0, 10, 1234567890},
                {"abc1234567890def", 3, 10, 1234567890},
                {"abc-1234567890def", 3, 11, -1234567890},
                {"abc+1234567890def", 3, 11, 1234567890},
        });
    }

    @Before
    public void setUp() throws Exception {
        tagNum = new Random().nextInt(1000) + 1;
        intField = new IntField(tagNum, string.getBytes(US_ASCII), offset, length);
    }

    @Test
    public void testTagNum() {
        assertEquals(tagNum, intField.getTagNum());
    }

    @Test
    public void testIntValue() {
        assertEquals(expectedValue, intField.intValue());
    }
}
