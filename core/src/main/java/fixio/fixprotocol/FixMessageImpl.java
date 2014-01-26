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

import fixio.fixprotocol.fields.AbstractField;
import fixio.fixprotocol.fields.FieldFactory;
import fixio.fixprotocol.fields.IntField;
import fixio.fixprotocol.fields.StringField;

import java.util.LinkedList;
import java.util.List;

/**
 * Read-only view implementation of received {@link FixMessage}.
 */
public class FixMessageImpl implements FixMessage {

    private final FixMessageHeader header = new FixMessageHeader();
    private final FixMessageTrailer trailer = new FixMessageTrailer();
    private final List<FixMessageFragment> body = new LinkedList<>();

    public FixMessageImpl add(int tagNum, byte[] value) {
        return add(tagNum, value, 0, value.length);
    }

    public FixMessageImpl add(int tagNum, byte[] value, int offset, int length) {
        assert (tagNum > 0) : "TagNum must be positive. Got " + tagNum;
        assert (value != null) : "Value must be specified.";
        AbstractField field = FieldFactory.valueOf(tagNum, value, offset, length);
        FieldType fieldType = FieldType.forTag(tagNum);
        switch (fieldType) {
            case BeginString:
                header.setBeginString(((StringField) field).getValue().intern());
                break;
            case CheckSum:
                int checksum = (value[offset] - '0') * 100 + (value[offset + 1] - '0') * 10 + (value[offset + 2] - '0');
                trailer.setCheckSum(checksum);
                break;
            case SenderCompID:
                header.setSenderCompID(((StringField) field).getValue());
                break;
            case TargetCompID:
                header.setTargetCompID(((StringField) field).getValue());
                break;
            case MsgSeqNum:
                header.setMsgSeqNum(((IntField) field).intValue());
                break;
            case MsgType:
                header.setMessageType(((StringField) field).getValue().intern());
                break;
            default:
                body.add(field);
                break;
        }
        return this;
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(FieldType fieldType) {
        FixMessageFragment field = getFirst(fieldType.tag());
        if (field instanceof AbstractField) {
            return (T) ((AbstractField) field).getValue();
        }
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

    @Override
    public String getMessageType() {
        return header.getMessageType();
    }

    public void setMessageType(String messageType) {
        header.setMessageType(messageType);
    }

    public int getChecksum() {
        return trailer.getCheckSum();
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FixMessageImpl{");
        sb.append("header=").append(header);
        sb.append(", body=").append(body);
        sb.append(", trailer=").append(trailer);
        sb.append('}');
        return sb.toString();
    }
}
