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
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.Assert.assertEquals;

public class FixSessionTest {

    private FixSession session;
    private String beginString;
    private String senderCompID;
    private String senderSubID;
    private String senderLocationID;
    private String targetCompID;
    private String targetSubID;
    private String targetLocationID;

    @Before
    public void setUp() throws Exception {

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
    public void testGetSessionId() {
        SessionId sessionId = session.getId();

        assertEquals("senderCompID", senderCompID, sessionId.getSenderCompID());
        assertEquals("senderSubID", senderSubID, sessionId.getSenderSubID());
        assertEquals("senderLocationID", senderLocationID, sessionId.getSenderLocationID());
        assertEquals("targetCompID", targetCompID, sessionId.getTargetCompID());
        assertEquals("targetSubID", targetSubID, sessionId.getTargetSubID());
        assertEquals("targetLocationID", targetLocationID, sessionId.getTargetLocationID());
    }

    @Test
    public void testPrepareOutgoing() throws Exception {
        int nextOutgoingMsgSeqNum = new Random().nextInt(100) + 100;
        session.setNextOutgoingMessageSeqNum(nextOutgoingMsgSeqNum);

        FixMessageBuilder messageBuilder = new FixMessageBuilderImpl();

        session.prepareOutgoing(messageBuilder);

        assertEquals(nextOutgoingMsgSeqNum + 1, session.getNextOutgoingMessageSeqNum());

        final FixMessageHeader header = messageBuilder.getHeader();

        assertEquals("nextOutgoingMsgSeqNum", nextOutgoingMsgSeqNum, header.getMsgSeqNum());
        assertEquals("beginString", beginString, header.getBeginString());
        assertEquals("senderCompID", senderCompID, header.getSenderCompID());
        assertEquals("senderSubID", senderSubID, header.getSenderSubID());
        assertEquals("senderLocationID", senderLocationID, header.getSenderLocationID());
        assertEquals("targetCompID", targetCompID, header.getTargetCompID());
        assertEquals("targetSubID", targetSubID, header.getTargetSubID());
        assertEquals("targetLocationID", targetLocationID, header.getTargetLocationID());
    }


}
