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
import fixio.fixprotocol.fields.FixedPointNumber;
import fixio.fixprotocol.fields.IntField;
import fixio.fixprotocol.fields.StringField;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;

import java.util.ArrayList;
import java.util.List;

public class FixMessageBuilderImpl implements FixMessage, FixMessageBuilder {

    private static final int DEFAULT_BODY_FIELD_COUNT = 16;
    private final FixMessageHeader header;
    private final FixMessageTrailer trailer;
    private final Int2ObjectArrayMap<FixMessageFragment> body;

    /**
     * Creates FixMessageBuilderImpl with expected body field count.
     * <p>
     * Providing expected capacity eliminates unnecessary growing of internal ArrayList storing body fields.
     * </p>
     *
     * @param expectedBodyFieldCount estimated maximum number of field in message body
     */
    private FixMessageBuilderImpl(int expectedBodyFieldCount) {
        this.header = new FixMessageHeader();
        this.trailer = new FixMessageTrailer();
        this.body = new Int2ObjectArrayMap<>(expectedBodyFieldCount);
    }

    /**
     * Creates FixMessageBuilderImpl with specified FixMessageHeader and  FixMessageTrailer.
     *
     * @param header  message header
     * @param trailer message trailer
     */
    public FixMessageBuilderImpl(FixMessageHeader header, final FixMessageTrailer trailer) {
        assert (header != null) : "FixMessageHeader is expected";
        assert (trailer != null) : "FixMessageTrailer is expected";
        this.header = header;
        this.trailer = trailer;
        this.body = new Int2ObjectArrayMap<>(DEFAULT_BODY_FIELD_COUNT);
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
     * @param messageType Value of message type (tag 35)
     * @see #DEFAULT_BODY_FIELD_COUNT
     */
    public FixMessageBuilderImpl(String messageType) {
        this();
        header.setMessageType(messageType);
    }

    /**
     * Creates FixMessageBuilderImpl with specified message type (tag 35)
     * and expected body field count.
     *
     * @param messageType            Value of message type (tag 35)
     * @param expectedBodyFieldCount estimated maximum number of field in message body
     */
    public FixMessageBuilderImpl(String messageType, int expectedBodyFieldCount) {
        this(expectedBodyFieldCount);
        header.setMessageType(messageType);
    }

    @Override
    public FixMessageBuilderImpl add(FieldType field, int value) {
        FieldListBuilderHelper.add(body, field, value);
        return this;
    }

    @Override
    public FixMessageBuilderImpl add(int tagNum, int value) {
        FieldListBuilderHelper.add(body, tagNum, value);
        return this;
    }

    @Override
    public FixMessageBuilder add(DataType type, int tagNum, int value) {
        FieldListBuilderHelper.add(body, type, tagNum, value);
        return this;
    }

    @Override
    public FixMessageBuilderImpl add(FieldType field, long value) {
        FieldListBuilderHelper.add(body, field, value);
        return this;
    }

    @Override
    public FixMessageBuilderImpl add(int tagNum, long value) {
        FieldListBuilderHelper.add(body, tagNum, value);
        return this;
    }

    @Override
    public FixMessageBuilder add(DataType type, int tagNum, long value) {
        FieldListBuilderHelper.add(body, type, tagNum, value);
        return this;
    }

    @Override
    public FixMessageBuilderImpl add(FieldType fieldType, String value) {
        FieldListBuilderHelper.add(body, fieldType, value);
        return this;
    }

    @Override
    public FixMessageBuilderImpl add(int tagNum, String value) {
        FieldListBuilderHelper.add(body, tagNum, value);
        return this;
    }

    @Override
    public FixMessageBuilder add(FieldType field, char value) {
        FieldListBuilderHelper.add(body, field, value);
        return this;
    }

    @Override
    public FixMessageBuilder add(DataType type, int tagNum, String value) {
        FieldListBuilderHelper.add(body, type, tagNum, value);
        return this;
    }

    @Override
    public FixMessageBuilderImpl add(FieldType fieldType, FixedPointNumber value) {
        FieldListBuilderHelper.add(body, fieldType, value);
        return this;
    }

    @Override
    public FixMessageBuilder add(int tagNum, FixedPointNumber value) {
        FieldListBuilderHelper.add(body, tagNum, value);
        return this;
    }

    @Override
    public FixMessageBuilder add(DataType type, int tagNum, FixedPointNumber value) {
        FieldListBuilderHelper.add(body, type, tagNum, value);
        return this;
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

    @Override
    public List<FixMessageFragment> getBody() {
        return new ArrayList<>(body.values());
    }

    @Override
    public void copyBody(List<? extends FixMessageFragment> body) {
        this.body.clear();
        for (FixMessageFragment fragment : body) {
            this.body.put(fragment.getTagNum(), fragment);
        }
    }

    @Override
    public String getString(int tagNum) {
        FixMessageFragment item = getFragment(tagNum);
        if (item == null) {
            return null;
        }
        if (item instanceof StringField) {
            return ((StringField) item).getValue();
        } else {
            throw new IllegalArgumentException("Tag " + tagNum + " is not a Field.");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(FieldType fieldType) {
        FixMessageFragment field = getFragment(fieldType.tag());
        if (field != null) {
            return (T) field.getValue();
        }
        return null;
    }

    @Override
    public String getString(FieldType field) {
        return getString(field.tag());
    }

    @Override
    public Character getChar(FieldType fieldType) {
        return getChar(fieldType.tag());
    }

    @Override
    public Character getChar(int tagNum) {
        FixMessageFragment field = getFragment(tagNum);
        if (field == null) {
            return null;
        }
        if (field instanceof CharField) {
            return ((CharField) field).getValue();
        } else {
            throw new IllegalArgumentException("Tag " + tagNum + " is not a Field.");
        }
    }

    @Override
    public Integer getInt(int tagNum) {
        FixMessageFragment field = getFragment(tagNum);
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

    @Override
    public void copyHeader(FixMessageHeader header) {
        this.header.setMessageType(header.getMessageType());
        this.header.setMsgSeqNum(header.getMsgSeqNum());
        this.header.setSenderCompID(header.getSenderCompID());
        this.header.setSenderSubID(header.getSenderSubID());
        this.header.setSenderLocationID(header.getSenderLocationID());
        this.header.setBeginString(header.getBeginString());
        this.header.setTargetCompID(header.getTargetCompID());
        this.header.setTargetSubID(header.getTargetSubID());
        this.header.setTargetLocationID(header.getTargetLocationID());
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

    private FixMessageFragment getFragment(int tagNum) {
        return body.get(tagNum);
    }

    private void addGroup(int tagNum, Group group) {
        GroupField g = (GroupField) getFragment(tagNum);
        if (g == null) {
            g = new GroupField(tagNum);
            body.put(tagNum, g);
        }
        g.add(group);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(512);
        final String sp = System.getProperty("line.separator");
        sb.append(sp);
        sb.append("header{").append(header).append("}").append(sp);
        sb.append("body{").append(body).append("}").append(sp);
        sb.append("trailer{").append(trailer).append('}').append(sp);
        return sb.toString();
    }
}
