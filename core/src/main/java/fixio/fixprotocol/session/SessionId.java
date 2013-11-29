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
package fixio.fixprotocol.session;

public class SessionId {

    private final String senderCompID;
    private final String targetCompID;
    private final String senderSubID;
    private final String targetSubID;

    private final int hash;

    public SessionId(String senderCompID, String targetCompID, String senderSubID, String targetSubID) {
        this.senderCompID = senderCompID;
        this.targetCompID = targetCompID;
        this.senderSubID = senderSubID;
        this.targetSubID = targetSubID;

        this.hash = calculateHash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionId sessionId = (SessionId) o;

        if (!senderCompID.equals(sessionId.senderCompID)) return false;
        if (senderSubID != null ? !senderSubID.equals(sessionId.senderSubID) : sessionId.senderSubID != null)
            return false;
        if (!targetCompID.equals(sessionId.targetCompID)) return false;
        if (targetSubID != null ? !targetSubID.equals(sessionId.targetSubID) : sessionId.targetSubID != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    private int calculateHash() {
        int result = senderCompID.hashCode();
        result = 31 * result + targetCompID.hashCode();
        result = 31 * result + (senderSubID != null ? senderSubID.hashCode() : 0);
        result = 31 * result + (targetSubID != null ? targetSubID.hashCode() : 0);
        return result;
    }
}
