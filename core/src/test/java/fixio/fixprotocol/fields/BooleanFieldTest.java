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
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class BooleanFieldTest {

    private int tag;

    @BeforeEach
    void setUp() {
        tag = new Random().nextInt(10000);
    }

    @Test
    void getBytesTrue() {
        BooleanField booleanField = new BooleanField(tag, true);
        byte[] bytes = booleanField.getBytes();
        assertThat(bytes.length).isEqualTo(1);
        assertThat(bytes[0]).isEqualTo('Y');
    }

    @Test
    void getBytesFalse() {
        BooleanField booleanField = new BooleanField(tag, false);
        byte[] bytes = booleanField.getBytes();
        assertThat(bytes.length).isEqualTo(1);
        assertThat(bytes[0]).isEqualTo('N');
    }
}
