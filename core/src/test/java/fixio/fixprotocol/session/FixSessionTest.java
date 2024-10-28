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
import fixio.fixprotocol.FixMessageBuilderImpl;
import fixio.fixprotocol.FixMessageHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.assertj.core.api.Assertions.assertThat;

class FixSessionTest {

    private FixSession session;
    private String beginString;
    private String senderCompID;
    private String senderSubID;
    private String senderLocationID;
    private String targetCompID;
    private String targetSubID;
    private String targetLocationID;

    @BeforeEach
    void setUp() {

        beginString = randomAscii(2);
        senderCompID = randomAscii(3);
        senderSubID = randomAscii(4);
        senderLocationID = randomAscii(4);
        targetCompID = randomAscii(5);
        targetSubID = randomAscii(6);
        targetLocationID = randomAscii(6);

        session = FixSession.newBuilder()
                .beginString(beginString)
                .senderCompID(senderCompID)
                .senderSubID(senderSubID)
                .senderLocationID(senderLocationID)
                .targetCompID(targetCompID)
                .targetSubID(targetSubID)
                .targetLocationID(targetLocationID)
                .build();
    }

    @Test
    void getSessionId() {
        SessionId sessionId = session.getId();

        assertThat(sessionId.getSenderCompID()).as("senderCompID").isEqualTo(senderCompID);
        assertThat(sessionId.getSenderSubID()).as("senderSubID").isEqualTo(senderSubID);
        assertThat(sessionId.getSenderLocationID()).as("senderLocationID").isEqualTo(senderLocationID);
        assertThat(sessionId.getTargetCompID()).as("targetCompID").isEqualTo(targetCompID);
        assertThat(sessionId.getTargetSubID()).as("targetSubID").isEqualTo(targetSubID);
        assertThat(sessionId.getTargetLocationID()).as("targetLocationID").isEqualTo(targetLocationID);
    }

    @Test
    void prepareOutgoing() {
        int nextOutgoingMsgSeqNum = new Random().nextInt(100) + 100;
        session.setNextOutgoingMessageSeqNum(nextOutgoingMsgSeqNum);

        FixMessageBuilder messageBuilder = new FixMessageBuilderImpl();

        session.prepareOutgoing(messageBuilder);

        assertThat(session.getNextOutgoingMessageSeqNum()).isEqualTo(nextOutgoingMsgSeqNum + 1);

        final FixMessageHeader header = messageBuilder.getHeader();

        assertThat(header.getMsgSeqNum()).as("nextOutgoingMsgSeqNum").isEqualTo(nextOutgoingMsgSeqNum);
        assertThat(header.getBeginString()).as("beginString").isEqualTo(beginString);
        assertThat(header.getSenderCompID()).as("senderCompID").isEqualTo(senderCompID);
        assertThat(header.getSenderSubID()).as("senderSubID").isEqualTo(senderSubID);
        assertThat(header.getSenderLocationID()).as("senderLocationID").isEqualTo(senderLocationID);
        assertThat(header.getTargetCompID()).as("targetCompID").isEqualTo(targetCompID);
        assertThat(header.getTargetSubID()).as("targetSubID").isEqualTo(targetSubID);
        assertThat(header.getTargetLocationID()).as("targetLocationID").isEqualTo(targetLocationID);
    }


}
