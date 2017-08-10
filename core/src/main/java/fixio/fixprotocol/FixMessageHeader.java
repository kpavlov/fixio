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

import fixio.fixprotocol.fields.DateTimeFormatterWrapper;

import java.time.ZonedDateTime;
import java.util.List;

public class FixMessageHeader {

    private String beginString;
    private String messageType;
    private int msgSeqNum;
    private ZonedDateTime sendingTime;
    private String senderCompID;
    private String senderSubID;
    private String senderLocationID;
    private String targetCompID;
    private String targetSubID;
    private String targetLocationID;
    private List<FixMessageFragment> customFields;
    private DateTimeFormatterWrapper dateTimeFormatter = null;

    public String getBeginString() {
        return beginString;
    }

    public void setBeginString(String beginString) {
        this.beginString = beginString;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSenderCompID() {
        return senderCompID;
    }

    public void setSenderCompID(String senderCompID) {
        this.senderCompID = senderCompID;
    }

    public String getSenderSubID() {
        return senderSubID;
    }

    public void setSenderSubID(String senderSubID) {
        this.senderSubID = senderSubID;
    }

    public String getSenderLocationID() {
        return senderLocationID;
    }

    public void setSenderLocationID(String senderLocationID) {
        this.senderLocationID = senderLocationID;
    }

    public String getTargetCompID() {
        return targetCompID;
    }

    public void setTargetCompID(String targetCompID) {
        this.targetCompID = targetCompID;
    }

    public String getTargetSubID() {
        return targetSubID;
    }

    public void setTargetSubID(String targetSubID) {
        this.targetSubID = targetSubID;
    }

    public String getTargetLocationID() {
        return targetLocationID;
    }

    public void setTargetLocationID(String targetLocationID) {
        this.targetLocationID = targetLocationID;
    }

    public int getMsgSeqNum() {
        return msgSeqNum;
    }

    public void setMsgSeqNum(int msgSeqNum) {
        this.msgSeqNum = msgSeqNum;
    }

    public ZonedDateTime getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(ZonedDateTime sendingTime) {
        this.sendingTime = sendingTime;
    }

    public List<FixMessageFragment> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<FixMessageFragment> customFields) {
        this.customFields = customFields;
    }

    public DateTimeFormatterWrapper getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public void setDateTimeFormatter(DateTimeFormatterWrapper dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("beginString='").append(beginString).append('\'');
        sb.append(", messageType='").append(messageType).append('\'');
        sb.append(", senderCompID='").append(senderCompID).append('\'');
        sb.append(", targetCompID='").append(targetCompID).append('\'');
        sb.append(", msgSeqNum=").append(msgSeqNum);
        if (senderSubID != null) {
            sb.append(", senderSubID='").append(senderSubID).append('\'');
        }
        if (senderLocationID != null) {
            sb.append(", senderLocationID='").append(senderLocationID).append('\'');
        }
        if (targetSubID != null) {
            sb.append(", targetSubID='").append(targetSubID).append('\'');
        }
        if (targetLocationID != null) {
            sb.append(", targetLocationID='").append(targetLocationID).append('\'');
        }
        if (customFields != null) {
            sb.append(customFields);
        }
        sb.append('}');
        return sb.toString();
    }
}
