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

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.assertj.core.api.Assertions.assertThat;

class FloatFieldTest {
    private FixedPointNumber value;
    private int tag;
    private FloatField field;

    @BeforeEach
    void setUp() {
        value = new FixedPointNumber(new Random().nextDouble(), 13);
        tag = new Random().nextInt();

        field = new FloatField(tag, value);
    }

    @Test
    void getBytes() {
        byte[] bytes = field.getBytes();

        byte[] expectedBytes = value.toString().getBytes(US_ASCII);

        assertThat(bytes).containsExactly(expectedBytes);
    }

    @Test
    void getDouble() {
        String valueStr = "203.03";
        byte[] val = valueStr.getBytes();
        value = new FixedPointNumber(val, 0, val.length);
        field = new FloatField(tag, value);
        assertThat(field.getValue().doubleValue()).isEqualTo(203.03);
        assertThat(field.floatValue()).isCloseTo(203.03f, Percentage.withPercentage(1));
        assertThat(field.getValue()).hasToString(valueStr);
    }
}
