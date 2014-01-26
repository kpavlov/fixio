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

public class BooleanField extends AbstractField<Boolean> {

    private final boolean value;
    private final byte[] TRUE_BYTES = new byte[]{(byte) 'Y'};
    private final byte[] FALSE_BYTES = new byte[]{(byte) 'N'};

    protected BooleanField(int tagNum, boolean value) {
        super(tagNum);
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public byte[] getBytes() {
        return (value) ? TRUE_BYTES : FALSE_BYTES;
    }

    public boolean booleanValue() {
        return value;
    }
}
