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

import java.util.Arrays;
import java.util.Random;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.assertj.core.api.Assertions.assertThat;

public class IntFieldFromBytesTest {

    private String string;
    private int offset;
    private int length;
    private int expectedValue;
    private IntField intField;
    private int tagNum;

    public void initIntFieldFromBytesTest(String string, int offset, int length, int expectedValue) {
        this.string = string;
        this.offset = offset;
        this.length = length;
        this.expectedValue = expectedValue;
    }

    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"1234567890", 0, 10, 1234567890},
                {"01234567890", 0, 11, 1234567890},
                {"abc1234567890def", 3, 10, 1234567890},
                {"abc-1234567890def", 3, 11, -1234567890},
                {"abc+1234567890def", 3, 11, 1234567890},
                {"abc01234567890def", 3, 11, 1234567890},
                {"abc-01234567890def", 3, 12, -1234567890},
                {"abc+01234567890def", 3, 12, 1234567890},
                {"abc001234567890def", 3, 12, 1234567890},
                {"abc-001234567890def", 3, 13, -1234567890},
                {"abc+001234567890def", 3, 13, 1234567890},
        });
    }

    @BeforeEach
    void setUp() throws Exception {
        tagNum = new Random().nextInt(1000) + 1;
        intField = new IntField(tagNum, string.getBytes(US_ASCII), offset, length);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void tagNum(String string, int offset, int length, int expectedValue) {
        initIntFieldFromBytesTest(string, offset, length, expectedValue);
        assertThat(intField.getTagNum()).isEqualTo(tagNum);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0}")
    void intValue(String string, int offset, int length, int expectedValue) {
        initIntFieldFromBytesTest(string, offset, length, expectedValue);
        assertThat(intField.intValue()).isEqualTo(expectedValue);
    }
}
