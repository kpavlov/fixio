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
package fixio.netty.pipeline;

import fixio.fixprotocol.*;

import static org.junit.Assert.*;

public final class FixMessageAsserts {

    private FixMessageAsserts() {
    }

    public static void assertLogonAck(FixMessage logonAck) {
        FixMessageHeader header = logonAck.getHeader();
        assertEquals("logon ack message type", MessageTypes.LOGON, header.getMessageType());
        assertNull("Username not expected in response.", logonAck.getString(FieldType.Username));
        assertNull("Password not expected in response", logonAck.getString(FieldType.Password));
        assertNotNull("Heartbeat interval", logonAck.getInt(FieldType.HeartBtInt));
        assertEquals("message SeqNum", 1, header.getMsgSeqNum());
    }

    public static void assertLogout(FixMessageBuilder fixMessage) {
        assertEquals("Logout message type", MessageTypes.LOGOUT, fixMessage.getHeader().getMessageType());
    }

    public static void assertResendRequest(FixMessageBuilderImpl fixMessage, int beginSeqNum, int endSeqNum) {
        assertEquals("ResendRequest message type", MessageTypes.RESEND_REQUEST, fixMessage.getHeader().getMessageType());

        assertEquals("BeginSeqNo", (Integer) beginSeqNum, fixMessage.getInt(FieldType.BeginSeqNo));
        assertEquals("EndSeqNo", (Integer) endSeqNum, fixMessage.getInt(FieldType.EndSeqNo));
    }

}
