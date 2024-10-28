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

import static org.assertj.core.api.Assertions.assertThat;

public final class FixMessageAsserts {

    private FixMessageAsserts() {
    }

    public static void assertLogonAck(FixMessage logonAck) {
        FixMessageHeader header = logonAck.getHeader();
        assertThat(header.getMessageType()).as("logon ack message type").isEqualTo(MessageTypes.LOGON);
        assertThat(logonAck.getString(FieldType.Username)).as("Username not expected in response.").isNull();
        assertThat(logonAck.getString(FieldType.Password)).as("Password not expected in response").isNull();
        assertThat(logonAck.getInt(FieldType.HeartBtInt)).as("Heartbeat interval").isNotNull();
        assertThat(header.getMsgSeqNum()).as("message SeqNum").isEqualTo(1);
    }

    public static void assertLogout(FixMessageBuilder fixMessage) {
        assertThat(fixMessage.getHeader().getMessageType()).as("Logout message type").isEqualTo(MessageTypes.LOGOUT);
    }

    public static void assertResendRequest(FixMessageBuilderImpl fixMessage, int beginSeqNum, int endSeqNum) {
        assertThat(fixMessage.getHeader().getMessageType()).as("ResendRequest message type").isEqualTo(MessageTypes.RESEND_REQUEST);

        assertThat(fixMessage.getInt(FieldType.BeginSeqNo)).as("BeginSeqNo").isEqualTo((Integer) beginSeqNum);
        assertThat(fixMessage.getInt(FieldType.EndSeqNo)).as("EndSeqNo").isEqualTo((Integer) endSeqNum);
    }

}
