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

import fixio.fixprotocol.FixConst;
import fixio.fixprotocol.FixMessageBuilder;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.fields.DateTimeFormatterWrapper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static fixio.fixprotocol.FixConst.TimeStampPrecision.MICROS;
import static fixio.fixprotocol.FixConst.TimeStampPrecision.MILLIS;
import static fixio.fixprotocol.FixConst.TimeStampPrecision.NANOS;
import static fixio.fixprotocol.FixConst.TimeStampPrecision.PICOS;
import static fixio.fixprotocol.FixConst.TimeStampPrecision.SECONDS;

public class FixSession {

    private static final AtomicIntegerFieldUpdater<FixSession> INCOMING_SEQ_NUM_UPDATER = AtomicIntegerFieldUpdater.newUpdater
            (FixSession.class, "nextIncomingMessageSeqNum");
    private final AtomicInteger nextOutgoingMessageSeqNum = new AtomicInteger();
    private final String beginString;
    private final String senderCompID;
    private final String senderSubID;
    private final String senderLocationID;
    private final String targetCompID;
    private final String targetLocationID;
    private final String targetSubID;
    private final String defaultApplVerID;
    private String defaultApplExtID = null;
    //
    private final SessionId sessionId;
    private volatile int nextIncomingMessageSeqNum;
    private DateTimeFormatterWrapper dateTimeFormatter = FixConst.DATE_TIME_FORMATTER_MILLIS;

    private FixSession(String beginString,
                       String senderCompID, String senderSubID, String senderLocationID,
                       String targetCompID, String targetSubID, String targetLocationID,
                       String defaultApplVerID){
        this.beginString = beginString;
        this.senderCompID = senderCompID;
        this.senderSubID = senderSubID;
        this.senderLocationID = senderLocationID;
        this.targetCompID = targetCompID;
        this.targetSubID = targetSubID;
        this.targetLocationID = targetLocationID;
        this.defaultApplVerID = defaultApplVerID;
        //
        this.sessionId = new SessionId(senderCompID, targetCompID, senderSubID, targetSubID, senderLocationID, targetLocationID);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getBeginString() {
        return beginString;
    }

    public String getSenderCompID() {
        return senderCompID;
    }

    public String getSenderSubID() {
        return senderSubID;
    }

    public String getSenderLocationID() {
        return senderLocationID;
    }

    public String getTargetCompID() {
        return targetCompID;
    }

    public String getTargetSubID() {
        return targetSubID;
    }

    public String getTargetLocationID() {
        return targetLocationID;
    }

    public String getDefaultApplVerID() {
        return defaultApplVerID;
    }

    public String getDefaultApplExtID() {
        return defaultApplExtID;
    }

    public void setDefaultApplExtID(String defaultApplExtID) {
        this.defaultApplExtID = defaultApplExtID;
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

    public DateTimeFormatterWrapper getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public void setDateTimeFormatter(DateTimeFormatterWrapper dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public void prepareOutgoing(FixMessageBuilder fixMessage) {
        FixMessageHeader header = fixMessage.getHeader();

        if (header.getBeginString() == null || "".equals(header.getBeginString())) {
            header.setBeginString(beginString);
        }
        if (header.getMsgSeqNum() == 0) {
            header.setMsgSeqNum(nextOutgoingMessageSeqNum.getAndIncrement());
        }
        //
        if (header.getSenderCompID() == null || "".equals(header.getSenderCompID())) {
            header.setSenderCompID(senderCompID);
        }
        if (header.getSenderSubID() == null || "".equals(header.getSenderSubID())) {
            header.setSenderSubID(senderSubID);
        }
        if (header.getSenderLocationID() == null || "".equals(header.getSenderLocationID())) {
            header.setSenderLocationID(senderLocationID);
        }
        //
        if (header.getTargetCompID() == null || "".equals(header.getTargetCompID())) {
            header.setTargetCompID(targetCompID);
        }
        if (header.getTargetSubID() == null || "".equals(header.getTargetSubID())) {
            header.setTargetSubID(targetSubID);
        }
        if (header.getTargetLocationID() == null || "".equals(header.getTargetLocationID())) {
            header.setTargetLocationID(targetLocationID);
        }
        //
        if (header.getDateTimeFormatter() == null) {
            header.setDateTimeFormatter(dateTimeFormatter);
        }
    }

    public SessionId getId() {
        return sessionId;
    }

    public static class Builder {

        private String beginString;
        private String senderCompId;
        private String senderSubId;
        private String senderLocationID;
        private String targetCompId;
        private String targetSubId;
        private String targetLocationID;
        private String defaultApplVerID;
        private String defaultApplExtID=null;
        private DateTimeFormatterWrapper dateTimeFormatter = FixConst.DATE_TIME_FORMATTER_MILLIS;

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

        public Builder senderLocationID(String senderLocationID) {
            this.senderLocationID = senderLocationID;
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

        public Builder targetLocationID(String targetLocationID) {
            this.targetLocationID = targetLocationID;
            return this;
        }

        public Builder defaultApplVerID(String defaultApplVerID) {
            this.defaultApplVerID = defaultApplVerID;
            return this;
        }

        public Builder defaultApplExtID(String defaultApplExtID) {
            this.defaultApplExtID = defaultApplExtID;
            return this;
        }

        public Builder timeStampPrecision(String timeStampPrecision) {
            if(SECONDS.toString().equals(timeStampPrecision)){
                this.dateTimeFormatter = FixConst.DATE_TIME_FORMATTER_SECONDS;
            }else if(MILLIS.toString().equals(timeStampPrecision)){
                this.dateTimeFormatter = FixConst.DATE_TIME_FORMATTER_MILLIS;
            }else if(MICROS.toString().equals(timeStampPrecision)){
                this.dateTimeFormatter = FixConst.DATE_TIME_FORMATTER_MICROS;
            }else if(NANOS.toString().equals(timeStampPrecision)){
                this.dateTimeFormatter = FixConst.DATE_TIME_FORMATTER_NANOS;
            }else if(PICOS.toString().equals(timeStampPrecision)){
                this.dateTimeFormatter = FixConst.DATE_TIME_FORMATTER_PICOS;
            }
            return this;
        }

        public FixSession build() {
            FixSession session = new FixSession(
                    beginString,
                    senderCompId,
                    senderSubId,
                    senderLocationID,
                    targetCompId,
                    targetSubId,
                    targetLocationID,
                    defaultApplVerID
            );
            session.setDateTimeFormatter(dateTimeFormatter);
            session.setDefaultApplExtID(defaultApplExtID);
            return session;
        }

    }
}
