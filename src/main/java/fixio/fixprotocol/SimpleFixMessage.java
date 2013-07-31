/*
 * Copyright 2013 The FIX.io Project
 *
 * The Netty Project licenses this file to you under the Apache License,
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

import java.util.ArrayList;
import java.util.List;

public class SimpleFixMessage implements FixMessage {

    public static final String FIX_4_0 = "FIX.4.0";
    public static final String FIX_4_1 = "FIX.4.1";
    public static final String FIX_4_2 = "FIX.4.2";
    public static final String FIX_4_3 = "FIX.4.3";
    public static final String FIX_4_4 = "FIX.4.4";
    private final List<Field> rawFields = new ArrayList<>();
    private final FixMessageHeader header = new FixMessageHeader();
    private final FixMessageTrailer trailer = new FixMessageTrailer();
    private final List<FixMessageFragment> bodyFields = new ArrayList<>();

    public SimpleFixMessage() {

    }

    public SimpleFixMessage(String messageType) {
        header.setMessageType(messageType);
    }

    @Override
    public void add(FieldType field, int value) {
        assert (field != null) : "Tag must be specified.";
        add(field, String.valueOf(value));
    }

    @Override
    public void add(int tagNum, int value) {
        assert (tagNum > 0) : "Tag must be positive.";
        add(tagNum, String.valueOf(value));
    }

    @Override
    public void add(FieldType field, String value) {
        assert (field != null) : "Tag must be specified.";
        assert (value != null) : "Value must be specified.";
        add(field.tag(), value);
    }

    @Override
    public void add(int tagNum, String value) {
        assert (tagNum > 0) : "Tag must be positive.";
        assert (value != null) : "Value  must be specified.";
        switch (tagNum) {
            case 8:
                header.setBeginString(value);
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
                header.setMessageType(value);
                break;
            default:
                bodyFields.add(new Field(tagNum, value));
                break;
        }
        rawFields.add(new Field(tagNum, value));
    }

    @Override
    public List<FixMessageFragment> getBodyFields() {
        return bodyFields;
    }

    public List<Field> getRawFields() {
        return rawFields;
    }

    @Override
    public String getString(int tagNum) {
        for (Field field : rawFields) {
            if (field.getTagNum() == tagNum) {
                return (field).getValue();
            }
        }
        return null;
    }

    @Override
    public String getString(FieldType field) {
        return getString(field.tag());
    }

    @Override
    public Integer getInt(int tagNum) {
        for (Field field : rawFields) {
            if (field.getTagNum() == tagNum) {
                return Integer.parseInt(field.getValue());
            }

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

    public int getChecksum() {
        return trailer.getCheckSum();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SimpleFixMessage{");
        sb.append("header=").append(header);
        sb.append(", rawFields=").append(rawFields);
        sb.append(", trailer=").append(trailer);
        sb.append('}');
        return sb.toString();
    }
}
