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
package fixio.netty.pipeline.client;

/**
 * Simple java bean FixSessionSettingsProvider implementation.
 * <p>
 * <strong>Thread-safety:</strong> This implementation is immutable and thread-safe.
 * </p>
 */
public class FixSessionSettingsProviderImpl implements FixSessionSettingsProvider {

    private final String beginString;
    private final String senderCompID;
    private final String senderSubID;
    private final String targetCompID;
    private final String targetSubID;
    private final boolean resetMsgSeqNum;
    private final int heartbeatIntervalSec;

    private FixSessionSettingsProviderImpl(Builder builder) {
        beginString = builder.beginString;
        senderCompID = builder.senderCompID;
        senderSubID = builder.senderSubID;
        targetCompID = builder.targetCompID;
        targetSubID = builder.targetSubID;
        resetMsgSeqNum = builder.resetMsgSeqNum;
        heartbeatIntervalSec = builder.heartbeatInterval;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(FixSessionSettingsProviderImpl copy) {
        Builder builder = new Builder();
        builder.beginString = copy.beginString;
        builder.senderCompID = copy.senderCompID;
        builder.senderSubID = copy.senderSubID;
        builder.targetCompID = copy.targetCompID;
        builder.targetSubID = copy.targetSubID;
        builder.resetMsgSeqNum = copy.resetMsgSeqNum;
        return builder;
    }

    @Override
    public String getSenderCompID() {
        return senderCompID;
    }

    @Override
    public String getSenderSubID() {
        return senderSubID;
    }

    @Override
    public String getTargetCompID() {
        return targetCompID;
    }

    @Override
    public String getTargetSubID() {
        return targetSubID;
    }

    @Override
    public String getBeginString() {
        return beginString;
    }

    @Override
    public boolean isResetMsgSeqNum() {
        return resetMsgSeqNum;
    }

    @Override
    public int getHeartbeatInterval() {
        return heartbeatIntervalSec;
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        throw new UnsupportedOperationException("Get custom property operation not supported");
    }

    public static final class Builder {
        private String beginString;
        private String senderCompID;
        private String senderSubID;
        private String targetCompID;
        private String targetSubID;
        private boolean resetMsgSeqNum;
        private int heartbeatInterval = 60;

        private Builder() {
        }

        public Builder beginString(String val) {
            beginString = val;
            return this;
        }

        public Builder senderCompID(String val) {
            senderCompID = val;
            return this;
        }

        public Builder senderSubID(String val) {
            senderSubID = val;
            return this;
        }

        public Builder targetCompID(String val) {
            targetCompID = val;
            return this;
        }

        public Builder targetSubID(String val) {
            targetSubID = val;
            return this;
        }

        public Builder resetMsgSeqNum(boolean val) {
            resetMsgSeqNum = val;
            return this;
        }

        public Builder heartbeatInterval(int val) {
            heartbeatInterval = val;
            return this;
        }

        public FixSessionSettingsProviderImpl build() {
            return new FixSessionSettingsProviderImpl(this);
        }
    }
}
