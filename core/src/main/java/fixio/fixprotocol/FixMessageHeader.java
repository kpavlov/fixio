/*
 * Copyright 2013 The FIX.io Project
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

public class FixMessageHeader {

    private String beginString;
    private String messageType;
    private String senderCompID;
    private String targetCompID;
    private int msgSeqNum;
    private String senderSubID;
    private String targetSubID;

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

    public String getTargetCompID() {
        return targetCompID;
    }

    public void setTargetCompID(String targetCompID) {
        this.targetCompID = targetCompID;
    }

    public int getMsgSeqNum() {
        return msgSeqNum;
    }

    public void setMsgSeqNum(int msgSeqNum) {
        this.msgSeqNum = msgSeqNum;
    }

    public String getSenderSubID() {
        return senderSubID;
    }

    public void setSenderSubID(String senderSubID) {
        this.senderSubID = senderSubID;
    }

    public String getTargetSubID() {
        return targetSubID;
    }

    public void setTargetSubID(String targetSubID) {
        this.targetSubID = targetSubID;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FixMessageHeader{");
        sb.append("beginString='").append(beginString).append('\'');
//        sb.append(", bodyLength=").append(bodyLength);
        sb.append(", messageType='").append(messageType).append('\'');
        sb.append(", senderCompID='").append(senderCompID).append('\'');
        sb.append(", targetCompID='").append(targetCompID).append('\'');
        sb.append(", msgSeqNum=").append(msgSeqNum);
        sb.append(", senderSubID='").append(senderSubID).append('\'');
        sb.append(", targetSubID='").append(targetSubID).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
