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

import java.math.BigDecimal;
import java.util.Random;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.Assert.assertArrayEquals;

public class FloatFieldTest {
    private BigDecimal value;
    private int tag;
    private FloatField field;

    @Before
    public void setUp() throws Exception {
        value = BigDecimal.valueOf(new Random().nextDouble());// it's ok to use it here;
        tag = new Random().nextInt();

        field = new FloatField(tag, value);
    }

    @Test
    public void testGetBytes() throws Exception {
        byte[] bytes = field.getBytes();

        assertArrayEquals(value.toPlainString().getBytes(US_ASCII), bytes);
    }
}
