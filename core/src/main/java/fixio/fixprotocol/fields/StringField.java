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

import fixio.Utils;

public class StringField extends AbstractField<String> {

    private final byte[] value;

    public StringField(int tagNum, String value) {
        super(tagNum);
        this.value = Utils.stringToBytesASCII(value);
    }

    public StringField(int tagNum, byte[] value) {
        super(tagNum);
        this.value = value;
    }

    public StringField(int tagNum, byte[] source, int offset, int length) {
        super(tagNum);
        this.value = new byte[length];
        System.arraycopy(source, offset, this.value, 0, length);
    }

    @Override
    public String getValue() {
        return Utils.bytesToStringASCII(value);
    }

    @Override
    public byte[] getBytes() {
        return value;
    }
}
