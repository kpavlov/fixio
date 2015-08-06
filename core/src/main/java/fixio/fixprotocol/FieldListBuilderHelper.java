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
package fixio.fixprotocol;

import fixio.fixprotocol.fields.CharField;
import fixio.fixprotocol.fields.FieldFactory;
import fixio.fixprotocol.fields.FixedPointNumber;

import java.util.Map;

/**
 * Helper class for managing {@link FieldListBuilder}.
 */
final class FieldListBuilderHelper {

    private FieldListBuilderHelper() {
    }

    // From Int

    static void add(Map<Integer, FixMessageFragment> map, DataType type, int tagNum, int value) {
        assert (tagNum > 0) : "Tag must be positive.";
        map.put(tagNum, FieldFactory.fromIntValue(type, tagNum, value));
    }

    static void add(Map<Integer, FixMessageFragment> map, int tagNum, int value) {
        assert (tagNum > 0) : "Tag must be positive.";
        map.put(tagNum, FieldFactory.fromIntValue(tagNum, value));
    }

    static void add(Map<Integer, FixMessageFragment> map, FieldType fieldType, int value) {
        assert (fieldType != null) : "Tag must be specified.";
        map.put(fieldType.tag(), FieldFactory.fromIntValue(fieldType.type(), fieldType.tag(), value));
    }

    // From Long

    static void add(Map<Integer, FixMessageFragment> map, DataType type, int tagNum, long value) {
        assert (tagNum > 0) : "Tag must be positive.";
        map.put(tagNum, FieldFactory.fromLongValue(type, tagNum, value));
    }

    static void add(Map<Integer, FixMessageFragment> map, int tagNum, long value) {
        assert (tagNum > 0) : "Tag must be positive.";
        map.put(tagNum, FieldFactory.fromLongValue(tagNum, value));
    }

    static void add(Map<Integer, FixMessageFragment> map, FieldType fieldType, long value) {
        assert (fieldType != null) : "Tag must be specified.";
        map.put(fieldType.tag(), FieldFactory.fromLongValue(fieldType.type(), fieldType.tag(), value));
    }

    // From String

    static void add(Map<Integer, FixMessageFragment> map, FieldType fieldType, String value) {
        assert (fieldType != null) : "Tag must be specified.";
        assert (value != null) : "Value must be specified.";
        map.put(fieldType.tag(), FieldFactory.fromStringValue(fieldType.type(), fieldType.tag(), value));
    }

    static void add(Map<Integer, FixMessageFragment> map, FieldType fieldType, char value) {
        if (fieldType.type() != DataType.CHAR) {
            throw new IllegalArgumentException("FieldType " + fieldType + " must be CHAR");
        }
        map.put(fieldType.tag(), new CharField(fieldType.tag(), value));
    }

    static void add(Map<Integer, FixMessageFragment> map, int tagNum, String value) {
        assert (tagNum > 0) : "TagNum must be positive. Got " + tagNum;
        assert (value != null) : "Value must be specified.";
        map.put(tagNum, FieldFactory.fromStringValue(tagNum, value));
    }

    static void add(Map<Integer, FixMessageFragment> map, DataType type, int tagNum, String value) {
        assert (tagNum > 0) : "TagNum must be positive. Got " + tagNum;
        assert (value != null) : "Value must be specified.";
        map.put(tagNum, FieldFactory.fromStringValue(type, tagNum, value));
    }

    // From FixedPointNumber

    static void add(Map<Integer, FixMessageFragment> map, FieldType fieldType, FixedPointNumber value) {
        assert (fieldType != null) : "Tag must be specified.";
        assert (value != null) : "Value must be specified.";
        map.put(fieldType.tag(), FieldFactory.fromFixedPointValue(fieldType.type(), fieldType.tag(), value));
    }

    static void add(Map<Integer, FixMessageFragment> map, int tagNum, FixedPointNumber value) {
        assert (tagNum > 0) : "TagNum must be positive. Got " + tagNum;
        assert (value != null) : "Value must be specified.";
        map.put(tagNum, FieldFactory.fromFixedPointValue(tagNum, value));
    }

    static void add(Map<Integer, FixMessageFragment> map, DataType type, int tagNum, FixedPointNumber value) {
        assert (tagNum > 0) : "TagNum must be positive. Got " + tagNum;
        assert (value != null) : "Value must be specified.";
        map.put(tagNum, FieldFactory.fromFixedPointValue(type, tagNum, value));
    }
}
