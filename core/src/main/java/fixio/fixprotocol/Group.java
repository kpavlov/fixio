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

import fixio.fixprotocol.fields.FixedPointNumber;
import fixio.fixprotocol.fields.StringField;

import java.util.*;

/**
 * Represents FIX Protocol field Group or Component - a sequence of Fields or other Groups.
 */
public class Group implements FieldListBuilder<Group> {

    private static final int DEFAULT_GROUP_SIZE = 8;
    private final Map<Integer, FixMessageFragment> contents;

    public Group(int expectedSize) {
        this.contents = new LinkedHashMap<>(expectedSize);
    }

    public Group() {
        this.contents = new LinkedHashMap<>(DEFAULT_GROUP_SIZE);
    }

    public void add(FixMessageFragment element) {
        contents.put(element.getTagNum(), element);
    }

    @Override
    public Group add(FieldType fieldType, String value) {
        contents.put(fieldType.tag(), new StringField(fieldType.tag(), value));
        return this;
    }

    @Override
    public Group add(int tagNum, String value) {
        FieldListBuilderHelper.add(contents, tagNum, value);
        return this;
    }

    @Override
    public Group add(DataType type, int tagNum, String value) {
        FieldListBuilderHelper.add(contents, type, tagNum, value);
        return this;
    }

    @Override
    public Group add(FieldType field, int value) {
        FieldListBuilderHelper.add(contents, field, value);
        return this;
    }

    @Override
    public Group add(int tagNum, int value) {
        FieldListBuilderHelper.add(contents, tagNum, value);
        return this;
    }

    @Override
    public Group add(DataType type, int tagNum, int value) {
        FieldListBuilderHelper.add(contents, tagNum, value);
        return this;
    }

    @Override
    public Group add(FieldType field, long value) {
        FieldListBuilderHelper.add(contents, field, value);
        return this;
    }

    @Override
    public Group add(int tagNum, long value) {
        FieldListBuilderHelper.add(contents, tagNum, value);
        return this;
    }

    @Override
    public Group add(DataType type, int tagNum, long value) {
        FieldListBuilderHelper.add(contents, type, tagNum, value);
        return this;
    }

    @Override
    public Group add(FieldType fieldType, FixedPointNumber value) {
        FieldListBuilderHelper.add(contents, fieldType, value);
        return this;
    }

    @Override
    public Group add(int tagNum, FixedPointNumber value) {
        FieldListBuilderHelper.add(contents, tagNum, value);
        return this;
    }

    @Override
    public Group add(DataType type, int tagNum, FixedPointNumber value) {
        FieldListBuilderHelper.add(contents, type, tagNum, value);
        return this;
    }

    public <T> T getValue(int tagNum) {
        FixMessageFragment field = contents.get(tagNum);
        if (field != null)
            return (T) field.getValue();
        return null;
    }

    public <T> T getValue(FieldType fieldType) {
        FixMessageFragment field = contents.get(fieldType.tag());
        if (field != null)
            return (T) field.getValue();
        return null;
    }

    @Override
    public Group newGroup(FieldType fieldType) {
        return newGroup(fieldType.tag());
    }

    @Override
    public Group newGroup(FieldType fieldType, int expectedGroupSize) {
        Group group = new Group(expectedGroupSize);
        addGroup(fieldType.tag(), group);
        return group;
    }

    @Override
    public Group newGroup(int tagNum) {
        Group group = new Group();
        addGroup(tagNum, group);
        return group;
    }

    @Override
    public Group newGroup(int tagNum, int expectedGroupSize) {
        Group group = new Group(expectedGroupSize);
        addGroup(tagNum, group);
        return group;
    }

    private void addGroup(int tagNum, Group group) {
        GroupField g = (GroupField) getFragment(tagNum);
        if (g == null) {
            g = new GroupField(tagNum);
            contents.put(tagNum, g);
        }
        g.add(group);
    }

    private FixMessageFragment getFragment(int tagNum) {
        return contents.get(tagNum);
    }

    public List<FixMessageFragment> getContents() {
        return new ArrayList<>(contents.values());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (FixMessageFragment fragment : contents.values()){
            int tagNum = fragment.getTagNum();
            sb.append(FieldType.forTag(tagNum) + "(" + tagNum + ")=" + fragment.getValue()).append(", ");
        }
        return sb.toString();
    }
}
