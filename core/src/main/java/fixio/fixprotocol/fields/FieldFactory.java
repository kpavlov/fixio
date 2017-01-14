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

import fixio.fixprotocol.DataType;
import fixio.fixprotocol.FieldType;

import java.text.ParseException;

public class FieldFactory {

    public static <F extends AbstractField> F valueOf(int tagNum, byte[] value) {
        return valueOf(tagNum, value, 0, value.length);
    }

    @SuppressWarnings("unchecked")
    public static <F extends AbstractField> F valueOf(int tagNum, byte[] value, int offset, int length) {
        if (tagNum <= 0) {
            throw new IllegalArgumentException("Invalid tagNum=" + tagNum);
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Value length must be positive but was " + length);
        }
        FieldType fieldType = FieldType.forTag(tagNum);
        try {
            final DataType dataType = fieldType.type();
            switch (dataType) {
                case STRING:
                    return (F) new StringField(tagNum, value, offset, length);
                case BOOLEAN:
                    switch (value[offset]) {
                        case 'Y':
                            return (F) new BooleanField(tagNum, true);
                        case 'N':
                            return (F) new BooleanField(tagNum, false);
                        default:
                            throw new ParseException("Invalid Boolean value. 'Y'/'N' is expected.", offset);
                    }
                case CHAR:
                    return (F) new CharField(tagNum, (char) value[offset]);
                case FLOAT:
                case PRICE:
                case PRICEOFFSET:
                case QTY:
                case AMT:
                case PERCENTAGE:
                    return (F) new FloatField(tagNum, value, offset, length);
                case INT:
                case LENGTH:
                case SEQNUM:
                case NUMINGROUP:
                    return (F) new IntField(tagNum, value, offset, length);
                case UTCTIMESTAMP:
                    return (F) new UTCTimestampField(tagNum, value, offset, length);
                default:
                    return (F) new StringField(tagNum, value, offset, length);
//                    throw new UnsupportedOperationException("Unsupported field type: " + fieldType
//                            + '(' + fieldType.type() + ')');
            }
        } catch (ParseException | NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for field " + fieldType + ": " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <F extends AbstractField<?>> F fromIntValue(DataType type, int tagNum, int value) {
        switch (type) {
            case STRING:
                return (F) new StringField(tagNum, Integer.toString(value));
            case CHAR:
                return (F) new CharField(tagNum, Character.forDigit(value, Character.MAX_RADIX));
            case FLOAT:
            case PRICE:
            case QTY:
            case PRICEOFFSET:
            case PERCENTAGE:
            case AMT:
//                return (F) new FloatField(tagNum, new FixedPointNumber(value));
            case INT:
            case LENGTH:
            case SEQNUM:
            case NUMINGROUP:
                return (F) new IntField(tagNum, value);
            case UTCTIMESTAMP:
                return (F) new UTCTimestampField(tagNum, value);
            case UTCTIMEONLY:
                return (F) new UTCTimeOnlyField(tagNum, value);
            case UTCDATEONLY:
                return (F) new UTCDateOnlyField(tagNum, value);
            default:
                throw new IllegalArgumentException("Value " + value + " is not applicable for field: " + tagNum
                        + '(' + type + ')');
        }
    }

    public static <F extends AbstractField<?>> F fromIntValue(int tagNum, int value) {
        FieldType fieldType = FieldType.forTag(tagNum);
        return fromIntValue(fieldType.type(), tagNum, value);
    }

    public static <F extends AbstractField<?>> F fromLongValue(int tagNum, long value) {
        FieldType fieldType = FieldType.forTag(tagNum);
        return fromLongValue(fieldType, value);
    }

    public static <F extends AbstractField<?>> F fromLongValue(FieldType fieldType, long value) {
        return fromLongValue(fieldType.type(), fieldType.tag(), value);
    }

    @SuppressWarnings("unchecked")
    public static <F extends AbstractField<?>> F fromLongValue(DataType type, int tagNum, long value) {
        switch (type) {
            case STRING:
                return (F) new StringField(tagNum, Long.toString(value));
            case FLOAT:
            case PRICE:
            case PRICEOFFSET:
            case PERCENTAGE:
            case AMT:
            case QTY:
//                return (F) new FloatField(tagNum, new FixedPointNumber(value));
            case INT:
            case LENGTH:
            case SEQNUM:
            case NUMINGROUP:
                return (F) new IntField(tagNum, (int) value);
            case UTCTIMESTAMP:
                return (F) new UTCTimestampField(tagNum, value);
            case UTCTIMEONLY:
                return (F) new UTCTimeOnlyField(tagNum, value);
            case UTCDATEONLY:
                return (F) new UTCDateOnlyField(tagNum, value);
            default:
                throw new IllegalArgumentException("Value " + value + " is not applicable for field: " + tagNum
                        + '(' + type + ')');
        }
    }

    public static <F extends AbstractField<?>> F fromStringValue(int tagNum, String value) {
        FieldType fieldType = FieldType.forTag(tagNum);
        return fromStringValue(fieldType.type(), tagNum, value);
    }

    public static <F extends AbstractField<?>> F fromStringValue(FieldType fieldType, String value) {
        return fromStringValue(fieldType.type(), fieldType.tag(), value);
    }

    @SuppressWarnings("unchecked")
    public static <F extends AbstractField<?>> F fromStringValue(DataType type, int tagNum, String value) {
        try {
            switch (type) {
                case BOOLEAN:
                    if ("Y".equals(value)) {
                        return (F) new BooleanField(tagNum, true);
                    } else if ("N".equals(value)) {
                        return (F) new BooleanField(tagNum, false);
                    }
                    return (F) new BooleanField(tagNum, Boolean.parseBoolean(value.toLowerCase()));
                case LOCALMKTDATE:
                case MONTHYEAR:
                case CHAR:
                case STRING:
                case CURRENCY:
                case COUNTRY:
                case EXCHANGE:
                case LANGUAGE:
                    return (F) new StringField(tagNum, value);
                case FLOAT:
                case PRICE:
                case PRICEOFFSET:
                case PERCENTAGE:
                case AMT:
                case QTY:
                    return (F) new FloatField(tagNum, new FixedPointNumber(value));
                case INT:
                case LENGTH:
                case SEQNUM:
                case NUMINGROUP:
                    return (F) new IntField(tagNum, Integer.parseInt(value));
                case UTCTIMESTAMP:
                    return (F) new UTCTimestampField(tagNum, value);
                case UTCTIMEONLY:
                    return (F) new UTCTimeOnlyField(tagNum, value);
                case UTCDATEONLY:
                    return (F) new UTCDateOnlyField(tagNum, value);
                default:
                    return (F) new StringField(tagNum, value);
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid value for field " + type + ": " + e.getMessage(), e);
        }
    }

    public static <F extends AbstractField<?>> F fromFixedPointValue(int tagNum, FixedPointNumber value) {
        FieldType fieldType = FieldType.forTag(tagNum);
        return fromFixedPointValue(fieldType, value);
    }

    public static <T, F extends AbstractField<?>> F fromFixedPointValue(FieldType fieldType, FixedPointNumber value) {
        return fromFixedPointValue(fieldType.type(), fieldType.tag(), value);
    }

    @SuppressWarnings("unchecked")
    public static <F extends AbstractField<?>> F fromFixedPointValue(DataType type, int tagNum, FixedPointNumber value) {
        switch (type) {
            case STRING:
                return (F) new StringField(tagNum, value.toString());
            case FLOAT:
            case PRICE:
            case PRICEOFFSET:
            case AMT:
            case PERCENTAGE:
            case QTY:
                return (F) new FloatField(tagNum, value);
            default:
                throw new IllegalArgumentException("Value " + value + " is not applicable for field : " + tagNum
                        + '(' + type + ')');
        }
    }
}
