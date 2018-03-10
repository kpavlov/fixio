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

import static org.junit.Assert.assertEquals;

public class BooleanFieldTest {

    private int tag;

    @Before
    public void setUp() {
        tag = new Random().nextInt(10000);
    }

    @Test
    public void testGetBytesTrue() {
        BooleanField booleanField = new BooleanField(tag, true);
        byte[] bytes = booleanField.getBytes();
        assertEquals(1, bytes.length);
        assertEquals('Y', bytes[0]);
    }

    @Test
    public void testGetBytesFalse() {
        BooleanField booleanField = new BooleanField(tag, false);
        byte[] bytes = booleanField.getBytes();
        assertEquals(1, bytes.length);
        assertEquals('N', bytes[0]);
    }
}
