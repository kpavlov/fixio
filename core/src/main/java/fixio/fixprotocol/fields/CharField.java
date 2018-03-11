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

import java.util.Objects;

public class CharField extends AbstractField<Character> {

    private final char value;

    public CharField(int tagNum, char value) {
        super(tagNum);
        this.value = value;
    }

    @Override
    public Character getValue() {
        return value;
    }

    @Override
    public byte[] getBytes() {
        return new byte[]{(byte) value};
    }

    public char charValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharField charField = (CharField) o;
        return value == charField.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
