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

import java.util.concurrent.atomic.AtomicInteger;

public class FixSession {

    private final AtomicInteger nextOutgoingMessageSeqNum = new AtomicInteger();
    private final AtomicInteger lastIncomingMessageSeqNum = new AtomicInteger();
    private final String beginString;
    private final String senderCompId;
    private final String senderSubId;
    private final String targetCompId;
    private final String targetSubId;

    private FixSession(String beginString, String senderCompId, String senderSubId, String targetCompId, String targetSubId) {
        this.beginString = beginString;
        this.senderCompId = senderCompId;
        this.senderSubId = senderSubId;
        this.targetCompId = targetCompId;
        this.targetSubId = targetSubId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getSenderCompId() {
        return senderCompId;
    }

    public String getSenderSubId() {
        return senderSubId;
    }

    public String getTargetCompId() {
        return targetCompId;
    }

    public String getTargetSubId() {
        return targetSubId;
    }

    public int getNextOutgoingMessageSeqNum() {
        return nextOutgoingMessageSeqNum.get();
    }

    public void setNextOutgoingMessageSeqNum(int nextOutgoingMessageSeqNum) {
        this.nextOutgoingMessageSeqNum.set(nextOutgoingMessageSeqNum);
    }

    public int getLastIncomingMessageSeqNum() {
        return lastIncomingMessageSeqNum.get();
    }

    public int getNextOutgoingMsgSeqNum() {
        return nextOutgoingMessageSeqNum.get();
    }

    public void prepareOutgoing(FixMessage fixMessage) {
        FixMessageHeader header = fixMessage.getHeader();
        header.setBeginString(beginString);
        header.setMessageType(fixMessage.getHeader().getMessageType());
        header.setMsgSeqNum(nextOutgoingMessageSeqNum.getAndIncrement());
        header.setSenderCompID(senderCompId);
        header.setSenderSubID(senderSubId);
        header.setTargetCompID(targetCompId);
        header.setTargetSubID(targetSubId);
    }

    public static class Builder {

        private String beginString;
        private String senderCompId;
        private String senderSubId;
        private String targetCompId;
        private String targetSubId;

        private Builder() {
        }

        public Builder beginString(String beginString) {
            this.beginString = beginString;
            return this;
        }

        public Builder senderCompId(String senderCompId) {
            this.senderCompId = senderCompId;
            return this;
        }

        public Builder senderSubId(String senderSubId) {
            this.senderSubId = senderSubId;
            return this;
        }

        public Builder targetCompId(String targetCompId) {
            this.targetCompId = targetCompId;
            return this;
        }

        public Builder targetSubId(String targetSubId) {
            this.targetSubId = targetSubId;
            return this;
        }

        public FixSession build() {
            return new FixSession(
                    beginString,
                    senderCompId,
                    senderSubId,
                    targetCompId,
                    targetSubId
            );
        }

    }
}
