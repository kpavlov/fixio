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

import java.util.Random;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.Assert.assertArrayEquals;

public class StringFieldTest {

    private String value;
    private StringField field;
    private int tag;

    @Before
    public void setUp() throws Exception {
        tag = new Random().nextInt();
        value = randomAscii(10);

        field = new StringField(tag, value);
    }

    @Test
    public void testGetBytes() throws Exception {
        byte[] bytes = field.getBytes();

        assertArrayEquals(value.getBytes(US_ASCII), bytes);
    }
}
