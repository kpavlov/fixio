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

import fixio.fixprotocol.fields.FieldFactory;
import fixio.fixprotocol.fields.FixedPointNumber;

import java.util.List;

/**
 * Helper class for managing {@link FieldListBuilder}.
 */
final class FieldListBuilderHelper {

    private FieldListBuilderHelper() {
    }

    // From Int

    static void add(List<FixMessageFragment> list, DataType type, int tagNum, int value) {
        assert (tagNum > 0) : "Tag must be positive.";
        list.add(FieldFactory.fromIntValue(type, tagNum, value));
    }

    static void add(List<FixMessageFragment> list, int tagNum, int value) {
        assert (tagNum > 0) : "Tag must be positive.";
        list.add(FieldFactory.fromIntValue(tagNum, value));
    }

    static void add(List<FixMessageFragment> list, FieldType fieldType, int value) {
        assert (fieldType != null) : "Tag must be specified.";
        list.add(FieldFactory.fromIntValue(fieldType.type(), fieldType.tag(), value));
    }

    // From Long

    static void add(List<FixMessageFragment> list, DataType type, int tagNum, long value) {
        assert (tagNum > 0) : "Tag must be positive.";
        list.add(FieldFactory.fromLongValue(type, tagNum, value));
    }

    static void add(List<FixMessageFragment> list, int tagNum, long value) {
        assert (tagNum > 0) : "Tag must be positive.";
        list.add(FieldFactory.fromLongValue(tagNum, value));
    }

    static void add(List<FixMessageFragment> list, FieldType fieldType, long value) {
        assert (fieldType != null) : "Tag must be specified.";
        list.add(FieldFactory.fromLongValue(fieldType.type(), fieldType.tag(), value));
    }

    // From String

    static void add(List<FixMessageFragment> list, FieldType fieldType, String value) {
        assert (fieldType != null) : "Tag must be specified.";
        assert (value != null) : "Value must be specified.";
        list.add(FieldFactory.fromStringValue(fieldType.type(), fieldType.tag(), value));
    }

    static void add(List<FixMessageFragment> list, int tagNum, String value) {
        assert (tagNum > 0) : "TagNum must be positive. Got " + tagNum;
        assert (value != null) : "Value must be specified.";
        list.add(FieldFactory.fromStringValue(tagNum, value));
    }

    static void add(List<FixMessageFragment> list, DataType type, int tagNum, String value) {
        assert (tagNum > 0) : "TagNum must be positive. Got " + tagNum;
        assert (value != null) : "Value must be specified.";
        list.add(FieldFactory.fromStringValue(type, tagNum, value));
    }

    // From FixedPointNumber

    static void add(List<FixMessageFragment> list, FieldType fieldType, FixedPointNumber value) {
        assert (fieldType != null) : "Tag must be specified.";
        assert (value != null) : "Value must be specified.";
        list.add(FieldFactory.fromFixedPointValue(fieldType.type(), fieldType.tag(), value));
    }

    static void add(List<FixMessageFragment> list, int tagNum, FixedPointNumber value) {
        assert (tagNum > 0) : "TagNum must be positive. Got " + tagNum;
        assert (value != null) : "Value must be specified.";
        list.add(FieldFactory.fromFixedPointValue(tagNum, value));
    }

    static void add(List<FixMessageFragment> list, DataType type, int tagNum, FixedPointNumber value) {
        assert (tagNum > 0) : "TagNum must be positive. Got " + tagNum;
        assert (value != null) : "Value must be specified.";
        list.add(FieldFactory.fromFixedPointValue(type, tagNum, value));
    }
}
