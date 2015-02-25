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
 * <p/>
 * <strong>Warning:</strong> This implementation is not thread-safe.
 */
public class SimpleFixSessionSettingsProvider implements FixSessionSettingsProvider {

    private String beginString;
    private String senderCompID;
    private String senderSubID;
    private String targetCompID;
    private String targetSubID;
    private boolean resetMsgSeqNum;
    private int heartbeatInterval;

    @Override
    public String getSenderCompID() {
        return senderCompID;
    }

    public void setSenderCompID(String senderCompID) {
        this.senderCompID = senderCompID;
    }

    @Override
    public String getSenderSubID() {
        return senderSubID;
    }

    public void setSenderSubID(String senderSubID) {
        this.senderSubID = senderSubID;
    }

    @Override
    public String getTargetCompID() {
        return targetCompID;
    }

    public void setTargetCompID(String targetCompID) {
        this.targetCompID = targetCompID;
    }

    @Override
    public String getTargetSubID() {
        return targetSubID;
    }

    public void setTargetSubID(String targetSubID) {
        this.targetSubID = targetSubID;
    }

    @Override
    public String getBeginString() {
        return beginString;
    }

    public void setBeginString(String beginString) {
        this.beginString = beginString;
    }

    @Override
    public boolean isResetMsgSeqNum() {
        return resetMsgSeqNum;
    }

    public void setResetMsgSeqNum(boolean resetMsgSeqNum) {
        this.resetMsgSeqNum = resetMsgSeqNum;
    }

    @Override
    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        throw new UnsupportedOperationException("Get custom property operation not supported");
    }
}
