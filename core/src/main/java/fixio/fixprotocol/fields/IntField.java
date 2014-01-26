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

public class IntField extends AbstractField<Integer> {

    private int value;

    protected IntField(int tagNum, int value) {
        super(tagNum);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public byte[] getBytes() {
        return String.valueOf(value).getBytes(US_ASCII);
    }

    public int intValue() {
        return value;
    }
}
