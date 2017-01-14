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
    private final String senderCompID;
    private final String senderSubID;
    private final String targetCompID;
    private final String targetSubID;
    private final SessionId sessionId;
    private volatile int nextIncomingMessageSeqNum;

    private FixSession(String beginString,
                       String senderCompID,
                       String senderSubID,
                       String targetCompID,
                       String targetSubID) {
        this.beginString = beginString;
        this.senderCompID = senderCompID;
        this.senderSubID = senderSubID;
        this.targetCompID = targetCompID;
        this.targetSubID = targetSubID;

        sessionId = new SessionId(senderCompID, targetCompID, senderSubID, targetSubID);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getSenderCompID() {
        return senderCompID;
    }

    public String getSenderSubID() {
        return senderSubID;
    }

    public String getTargetCompID() {
        return targetCompID;
    }

    public String getTargetSubID() {
        return targetSubID;
    }

    public int getNextOutgoingMessageSeqNum() {
        return nextOutgoingMessageSeqNum.get();
    }

    public void setNextOutgoingMessageSeqNum(int nextOutgoingMessageSeqNum) {
        this.nextOutgoingMessageSeqNum.set(nextOutgoingMessageSeqNum);
    }

    public int getNextIncomingMessageSeqNum() {
        return nextIncomingMessageSeqNum;
    }

    public void setNextIncomingMessageSeqNum(int nextIncomingMessageSeqNum) {
        this.nextIncomingMessageSeqNum = nextIncomingMessageSeqNum;
    }

    public int getNextOutgoingMsgSeqNum() {
        return nextOutgoingMessageSeqNum.get();
    }

    public boolean checkAndIncrementIncomingSeqNum(final int num) {
        return INCOMING_SEQ_NUM_UPDATER.compareAndSet(this, num, num + 1);
    }

    public void prepareOutgoing(FixMessageBuilder fixMessage) {
        FixMessageHeader header = fixMessage.getHeader();

        if (header.getBeginString() == null || "".equals(header.getBeginString())) {
            header.setBeginString(beginString);
        }
        if (header.getMsgSeqNum() == 0) {
            header.setMsgSeqNum(nextOutgoingMessageSeqNum.getAndIncrement());
        }
        if (header.getSenderCompID() == null || "".equals(header.getSenderCompID())) {
            header.setSenderCompID(senderCompID);
        }
        if (header.getSenderSubID() == null || "".equals(header.getSenderSubID())) {
            header.setSenderSubID(senderSubID);
        }
        if (header.getTargetCompID() == null || "".equals(header.getTargetCompID())) {
            header.setTargetCompID(targetCompID);
        }
        if (header.getTargetSubID() == null || "".equals(header.getTargetSubID())) {
            header.setTargetSubID(targetSubID);
        }

        header.setMessageType(header.getMessageType());
    }

    public SessionId getId() {
        return sessionId;
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
