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

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.Assert.assertEquals;

public class CharFieldTest {

    private char ch;
    private int tag;
    private CharField charField;

    @Before
    public void setUp() throws Exception {
        ch = randomAscii(1).charAt(0);
        tag = new Random().nextInt();

        charField = new CharField(tag, ch);
    }

    @Test
    public void testGetBytes() {
        byte[] bytes = charField.getBytes();
        assertEquals(1, bytes.length);
        assertEquals((byte) ch, bytes[0]);
    }
}
