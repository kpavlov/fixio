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
import fixio.fixprotocol.fields.IntField;
import fixio.fixprotocol.fields.StringField;

import java.util.ArrayList;
import java.util.List;

public class FixMessageBuilderImpl implements FixMessage, FixMessageBuilder {

    private static final int DEFAULT_BODY_FIELD_COUNT = 16;
    private final FixMessageHeader header;
    private final FixMessageTrailer trailer;
    private final List<FixMessageFragment> body;

    /**
     * Creates FixMessageBuilderImpl with expected body field count.
     * <p/>
     * Providing expected capacity eliminates unnecessary growing of internal ArrayList storing body fields.
     */
    public FixMessageBuilderImpl(int expectedBodyFieldCount) {
        header = new FixMessageHeader();
        trailer = new FixMessageTrailer();
        body = new ArrayList<>(expectedBodyFieldCount);
    }

    /**
     * Creates FixMessageBuilderImpl with specified FixMessageHeader and  FixMessageTrailer.
     */
    public FixMessageBuilderImpl(FixMessageHeader header, FixMessageTrailer trailer) {
        this.header = header;
        this.trailer = trailer;
        body = new ArrayList<>(DEFAULT_BODY_FIELD_COUNT);
    }

    /**
     * Creates FixMessageBuilderImpl with default expected body field count.
     *
     * @see #DEFAULT_BODY_FIELD_COUNT
     */
    public FixMessageBuilderImpl() {
        this(DEFAULT_BODY_FIELD_COUNT);
    }

    /**
     * Creates FixMessageBuilderImpl with specified message type (tag 35)
     * and default expected body field count.
     *
     * @see #DEFAULT_BODY_FIELD_COUNT
     */
    public FixMessageBuilderImpl(String messageType) {
        this();
        header.setMessageType(messageType);
    }

    /**
     * Creates FixMessageBuilderImpl with specified message type (tag 35)
     * and expected body field count.
     */
    public FixMessageBuilderImpl(String messageType, int expectedBodyFieldCount) {
        this(expectedBodyFieldCount);
        header.setMessageType(messageType);
    }

    @Override
    public FixMessageBuilderImpl add(FieldType field, int value) {
        assert (field != null) : "Tag must be specified.";

        return add(field, String.valueOf(value));
    }

    @Override
    public FixMessageBuilderImpl add(int tagNum, int value) {
        assert (tagNum > 0) : "Tag must be positive.";
        body.add(FieldFactory.fromIntValue(tagNum, value));
        return this;
    }

    public FixMessageBuilderImpl add(FieldType fieldType, FixedPointNumber value) {
        assert (fieldType != null) : "Tag must be specified.";
        assert (value != null) : "Value must be specified.";
        body.add(FieldFactory.fromFixedPointValue(fieldType, value));
        return this;
    }

    @Override
    public FixMessageBuilderImpl add(FieldType fieldType, String value) {
        assert (fieldType != null) : "Tag must be specified.";
        assert (value != null) : "Value must be specified.";
        body.add(FieldFactory.fromStringValue(fieldType, value));
        return this;
    }

    @Override
    public FixMessageBuilderImpl add(int tagNum, String value) {
        assert (tagNum > 0) : "TagNum must be positive. Got " + tagNum;
        assert (value != null) : "Value must be specified.";
        body.add(FieldFactory.fromStringValue(tagNum, value));
        return this;
    }

    @Override
    public Group newGroup(FieldType fieldType) {
        return newGroup(fieldType.tag());
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

    @Override
    public List<FixMessageFragment> getBody() {
        return body;
    }

    @Override
    public String getString(int tagNum) {
        FixMessageFragment item = getFirst(tagNum);
        if (item == null) {
            return null;
        }
        if (item instanceof StringField) {
            return ((StringField) item).getValue();
        } else {
            throw new IllegalArgumentException("Tag " + tagNum + " is not a Field.");
        }
    }

    @Override
    public <T> T getValue(FieldType field) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String getString(FieldType field) {
        return getString(field.tag());
    }

    @Override
    public Integer getInt(int tagNum) {
        FixMessageFragment field = getFirst(tagNum);
        if (field instanceof IntField) {
            return ((IntField) field).getValue();
        }
        return null;
    }

    @Override
    public Integer getInt(FieldType field) {
        return getInt(field.tag());
    }

    @Override
    public FixMessageHeader getHeader() {
        return header;
    }

    public int getMsgSeqNum() {
        return header.getMsgSeqNum();
    }

    public String getTargetCompID() {
        return header.getTargetCompID();
    }

    public String getSenderCompID() {
        return header.getSenderCompID();
    }

    public String getBeginString() {
        return header.getBeginString();
    }

    @Override
    public String getMessageType() {
        return header.getMessageType();
    }

    public void setMessageType(String messageType) {
        header.setMessageType(messageType);
    }

    public List<Group> getGroups(int tagNum) {
        FixMessageFragment fragment = getFirst(tagNum);
        if (fragment instanceof GroupField) {
            return ((GroupField) fragment).getGroups();
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FixMessageBuilderImpl{");
        sb.append("header=").append(header);
        sb.append(", body=").append(body);
        sb.append(", trailer=").append(trailer);
        sb.append('}');
        return sb.toString();
    }

    private FixMessageFragment getFirst(int tagNum) {
        for (int i = 0; i < body.size(); i++) {
            FixMessageFragment item = body.get(i);
            if (item.getTagNum() == tagNum) {
                return item;
            }
        }
        return null;
    }

    private FixMessageFragment getLast(int tagNum) {
        for (int i = body.size() - 1; i >= 0; i--) {
            FixMessageFragment item = body.get(i);
            if (item.getTagNum() == tagNum) {
                return item;
            }
        }
        return null;
    }

    private void addGroup(int tagNum, Group group) {
        GroupField g = (GroupField) getLast(tagNum);
        if (g == null) {
            g = new GroupField(tagNum);
            body.add(g);
        }
        g.add(group);
    }
}
