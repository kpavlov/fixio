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

import java.text.ParseException;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class IntField extends AbstractField<Integer> {

    private final int value;

    protected IntField(int tagNum, byte[] bytes, int offset, int length) throws ParseException {
        super(tagNum);
        int index = offset;
        int sign = 1;
        switch (bytes[offset]) {
            case '-':
                sign = -1;
                index++;
                break;
            case '+':
                sign = +1;
                index++;
                break;
            default:
        }
        int tempValue = 0;
        for (int i = index; i < offset + length; i++) {
            int digit = (bytes[i] - '0');
            if (digit > 9 || digit < 0) {
                throw new ParseException("Unparseable int: " + new String(bytes, offset, length, US_ASCII), i);
            }
            tempValue = tempValue * 10 + digit;
        }
        this.value = tempValue * sign;
    }

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
