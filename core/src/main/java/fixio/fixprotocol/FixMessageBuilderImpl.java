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
import fixio.fixprotocol.fields.IntField;
import fixio.fixprotocol.fields.StringField;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class FixMessageBuilderImpl implements FixMessage, FixMessageBuilder {

    private final FixMessageHeader header = new FixMessageHeader();
    private final FixMessageTrailer trailer = new FixMessageTrailer();
    private final List<FixMessageFragment> body = new LinkedList<>();

    public FixMessageBuilderImpl() {
    }

    public FixMessageBuilderImpl(String messageType) {
        header.setMessageType(messageType);
    }

    public FixMessageBuilderImpl add(FieldType field, int value) {
        assert (field != null) : "Tag must be specified.";
        return add(field, String.valueOf(value));
    }

    public FixMessageBuilderImpl add(int tagNum, int value) {
        assert (tagNum > 0) : "Tag must be positive.";
        return add(tagNum, String.valueOf(value));
    }

    public FixMessageBuilderImpl add(FieldType field, String value) {
        assert (field != null) : "Tag must be specified.";
        assert (value != null) : "Value must be specified.";
        return add(field.tag(), value);
    }

    public FixMessageBuilderImpl add(int tagNum, String value) {
        assert (tagNum > 0) : "TagNum must be positive. Got " + tagNum;
        assert (value != null) : "Value must be specified.";
        switch (tagNum) {
            case 8:
                header.setBeginString(value.intern());
                break;
            case 10:
                trailer.setCheckSum(Integer.parseInt(value));
                break;
            case 49:
                header.setSenderCompID(value);
                break;
            case 56:
                header.setTargetCompID(value);
                break;
            case 34:
                header.setMsgSeqNum(Integer.parseInt(value));
                break;
            case 35:
                header.setMessageType(value.intern());
                break;
            default:
                body.add(FieldFactory.valueOf(tagNum, value.getBytes(StandardCharsets.US_ASCII)));
                break;
        }
        return this;
    }

    public Group newGroup(FieldType fieldType) {
        return newGroup(fieldType.tag());
    }

    public Group newGroup(int tagNum) {
        Group group = new Group(tagNum);
        GroupField g = (GroupField) getFirst(tagNum);
        if (g == null) {
            g = new GroupField(tagNum);
            body.add(g);
        }
        g.add(group);
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
        return null;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FixMessageBuilderImpl{");
        sb.append("header=").append(header);
        sb.append(", body=").append(body);
        sb.append(", trailer=").append(trailer);
        sb.append('}');
        return sb.toString();
    }

    public List<Group> getGroups(int tagNum) {
        FixMessageFragment fragment = getFirst(tagNum);
        if (fragment instanceof GroupField) {
            return ((GroupField) fragment).getGroups();
        }
        return null;
    }

    private FixMessageFragment getFirst(int tagNum) {
        for (FixMessageFragment item : body) {
            if (item.getTagNum() == tagNum) {
                return item;
            }
        }
        return null;
    }
}
