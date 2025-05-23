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

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Random;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class IntFieldTest {

    @Test
    void getBytes() {
        int value = new Random().nextInt();
        int tag = new Random().nextInt();

        IntField field = new IntField(tag, value);
        byte[] bytes = field.getBytes();

        assertThat(bytes).containsExactly(String.valueOf(value).getBytes(US_ASCII));
    }

    @Test
    void failParseNonInteger() {
        assertThatExceptionOfType(ParseException.class).isThrownBy(() -> {
            new IntField(10, "12a345".getBytes(US_ASCII), 0, 6);
        });
    }
}
