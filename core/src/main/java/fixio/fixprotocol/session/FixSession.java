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

package fixio.fixprotocol.session;

import fixio.fixprotocol.FixMessageBuilder;
import fixio.fixprotocol.FixMessageHeader;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class FixSession {

    private static final AtomicIntegerFieldUpdater<FixSession> INCOMING_SEQ_NUM_UPDATER = AtomicIntegerFieldUpdater.newUpdater
            (FixSession.class, "nextIncomingMessageSeqNum");
    private final AtomicInteger nextOutgoingMessageSeqNum = new AtomicInteger();
    private final String beginString;
    private final String senderCompId;
    private final String senderSubId;
    private final String targetCompId;
    private final String targetSubId;
    private volatile int nextIncomingMessageSeqNum;

    private FixSession(String beginString,
                       String senderCompId,
                       String senderSubId,
                       String targetCompId,
                       String targetSubId,
                       int nextIncomingSeqNum) {
        this.beginString = beginString;
        this.senderCompId = senderCompId;
        this.senderSubId = senderSubId;
        this.targetCompId = targetCompId;
        this.targetSubId = targetSubId;
        this.nextIncomingMessageSeqNum = nextIncomingSeqNum;
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

    public void setNextIncomingMessageSeqNum(int nextIncomingMessageSeqNum) {
        this.nextIncomingMessageSeqNum = nextIncomingMessageSeqNum;
    }

    public int getNextIncomingMessageSeqNum() {
        return nextIncomingMessageSeqNum;
    }

    public int getNextOutgoingMsgSeqNum() {
        return nextOutgoingMessageSeqNum.get();
    }

    public boolean checkIncomingSeqNum(final int num) {
        return INCOMING_SEQ_NUM_UPDATER.compareAndSet(this, num, num + 1);
    }

    public void prepareOutgoing(FixMessageBuilder fixMessage) {
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
        private int nextIncomingSeqNum;

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

        public Builder nextIncomingSeqNum(int seqNum) {
            this.nextIncomingSeqNum = seqNum;
            return this;
        }

        public FixSession build() {
            return new FixSession(
                    beginString,
                    senderCompId,
                    senderSubId,
                    targetCompId,
                    targetSubId,
                    nextIncomingSeqNum
            );
        }

    }
}
