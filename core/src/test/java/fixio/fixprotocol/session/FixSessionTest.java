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
import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals(senderCompID, sessionId.getSenderCompID(), "senderCompID");
        assertEquals(senderSubID, sessionId.getSenderSubID(), "senderSubID");
        assertEquals(senderLocationID, sessionId.getSenderLocationID(), "senderLocationID");
        assertEquals(targetCompID, sessionId.getTargetCompID(), "targetCompID");
        assertEquals(targetSubID, sessionId.getTargetSubID(), "targetSubID");
        assertEquals(targetLocationID, sessionId.getTargetLocationID(), "targetLocationID");
    }

    @Test
    void prepareOutgoing() {
        int nextOutgoingMsgSeqNum = new Random().nextInt(100) + 100;
        session.setNextOutgoingMessageSeqNum(nextOutgoingMsgSeqNum);

        FixMessageBuilder messageBuilder = new FixMessageBuilderImpl();

        session.prepareOutgoing(messageBuilder);

        assertEquals(nextOutgoingMsgSeqNum + 1, session.getNextOutgoingMessageSeqNum());

        final FixMessageHeader header = messageBuilder.getHeader();

        assertEquals(nextOutgoingMsgSeqNum, header.getMsgSeqNum(), "nextOutgoingMsgSeqNum");
        assertEquals(beginString, header.getBeginString(), "beginString");
        assertEquals(senderCompID, header.getSenderCompID(), "senderCompID");
        assertEquals(senderSubID, header.getSenderSubID(), "senderSubID");
        assertEquals(senderLocationID, header.getSenderLocationID(), "senderLocationID");
        assertEquals(targetCompID, header.getTargetCompID(), "targetCompID");
        assertEquals(targetSubID, header.getTargetSubID(), "targetSubID");
        assertEquals(targetLocationID, header.getTargetLocationID(), "targetLocationID");
    }


}
