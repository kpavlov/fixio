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

import fixio.fixprotocol.FieldType;

import java.text.ParseException;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class FieldFactory {

    public static <T extends AbstractField> T valueOf(int tagNum, byte[] value) {
        return valueOf(tagNum, value, 0, value.length);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractField> T valueOf(int tagNum, byte[] value, int offset, int length) {
        if (tagNum <= 0) {
            throw new IllegalArgumentException("Invalid tagNum=" + tagNum);
        }
        FieldType fieldType = FieldType.forTag(tagNum);
        try {
            switch (fieldType.type()) {
                case STRING:
                    return (T) new StringField(tagNum, new String(value, offset, length, US_ASCII));
                case BOOLEAN:
                    return (T) new BooleanField(tagNum, (value[offset] == 'Y'));
                case CHAR:
                    return (T) new CharField(tagNum, (char) value[offset]);
                case FLOAT:
                case PRICE:
                case QTY:
                    return (T) new FloatField(tagNum, value, offset, length);
                case INT:
                case LENGTH:
                case SEQNUM:
                case NUMINGROUP:
                    return (T) new IntField(tagNum, Integer.parseInt(new String(value, offset, length, US_ASCII)));
                case UTCTIMESTAMP:
                    return (T) new UTCTimestampField(tagNum, value, offset, length);
                default:
                    throw new UnsupportedOperationException("Unsupported field type: " + fieldType
                            + '(' + fieldType.type() + ')');
            }
        } catch (ParseException | NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for field " + fieldType + ": " + e.getMessage(), e);
        }
    }
}
