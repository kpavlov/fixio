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

import static java.nio.charset.StandardCharsets.US_ASCII;

public class FloatField extends AbstractField<FixedPointNumber> {

    private final FixedPointNumber value;

    protected FloatField(int tagNum, FixedPointNumber value) {
        super(tagNum);
        this.value = value;
    }

    protected FloatField(int tagNum, byte[] value, int offset, int length) {
        super(tagNum);
        this.value = new FixedPointNumber(value, offset, length);
    }

    @Override
    public FixedPointNumber getValue() {
        return value;
    }

    @Override
    public byte[] getBytes() {
        String s = value.toString();
        return s.getBytes(US_ASCII);
    }

    public float floatValue() {
        return value.floatValue();
    }
}
