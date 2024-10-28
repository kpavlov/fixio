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

import fixio.fixprotocol.FieldType;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import fixio.fixprotocol.FixMessageBuilderImpl;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.MessageTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public final class FixMessageAsserts {

    private FixMessageAsserts() {
    }

    public static void assertLogonAck(FixMessage logonAck) {
        FixMessageHeader header = logonAck.getHeader();
        assertEquals(MessageTypes.LOGON, header.getMessageType(), "logon ack message type");
        assertNull(logonAck.getString(FieldType.Username), "Username not expected in response.");
        assertNull(logonAck.getString(FieldType.Password), "Password not expected in response");
        assertNotNull(logonAck.getInt(FieldType.HeartBtInt), "Heartbeat interval");
        assertEquals(1, header.getMsgSeqNum(), "message SeqNum");
    }

    public static void assertLogout(FixMessageBuilder fixMessage) {
        assertEquals(MessageTypes.LOGOUT, fixMessage.getHeader().getMessageType(), "Logout message type");
    }

    public static void assertResendRequest(FixMessageBuilderImpl fixMessage, int beginSeqNum, int endSeqNum) {
        assertEquals(MessageTypes.RESEND_REQUEST, fixMessage.getHeader().getMessageType(), "ResendRequest message type");

        assertEquals((Integer) beginSeqNum, fixMessage.getInt(FieldType.BeginSeqNo), "BeginSeqNo");
        assertEquals((Integer) endSeqNum, fixMessage.getInt(FieldType.EndSeqNo), "EndSeqNo");
    }

}
