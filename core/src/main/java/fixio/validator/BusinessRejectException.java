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
package fixio.validator;

/**
 * Exception thrown by FixMessageValidator or FixMessageHandler to indicate application-level error.
 */
public class BusinessRejectException extends Exception {

    /**
     * MsgSeqNum of rejected message
     */
    private final int refSeqNum;

    /**
     * The MsgType of the FIX message being referenced.
     */
    private final String refMsgType;

    /**
     * The value of the business-level "ID" field on the message being referenced.
     * Required unless the corresponding ID field (see list above) was not specified.
     */
    private final int businessRejectReason;

    /**
     * Where possible, message to explain reason for rejection.
     */
    private final String text;

    public BusinessRejectException(int refSeqNum, String refMsgType, int businessRejectReason, String text) {
        super(text);
        this.refSeqNum = refSeqNum;
        this.refMsgType = refMsgType;
        this.businessRejectReason = businessRejectReason;
        this.text = text;
    }

    public int getRefSeqNum() {
        return refSeqNum;
    }

    public String getRefMsgType() {
        return refMsgType;
    }

    public int getBusinessRejectReason() {
        return businessRejectReason;
    }

    public String getText() {
        return text;
    }
}
