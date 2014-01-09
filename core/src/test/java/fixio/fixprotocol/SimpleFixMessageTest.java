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
package fixio.fixprotocol;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class SimpleFixMessageTest {

    private SimpleFixMessage fixMessage;

    @Before
    public void setUp() throws Exception {
        fixMessage = new SimpleFixMessage();
    }

    @Test
    public void testHeaderFields() {
        String beginString = randomAscii(2);
        String senderCompID = randomAscii(3);
        String targetCompID = randomAscii(4);
        String msgType = randomAscii(5);

        fixMessage.getHeader().setBeginString(beginString);
        fixMessage.setMessageType(msgType);
        fixMessage.getHeader().setSenderCompID(senderCompID);
        fixMessage.getHeader().setTargetCompID(targetCompID);

        //SimpleFixMessage fixMessage = builder.build();

        assertEquals("beginString", beginString, fixMessage.getHeader().getBeginString());
        assertEquals("msgType", msgType, fixMessage.getMessageType());
        assertEquals("senderCompID", senderCompID, fixMessage.getHeader().getSenderCompID());
        assertEquals("targetCompID", targetCompID, fixMessage.getHeader().getTargetCompID());
    }

    @Test
    public void testWithGroups() {
        SimpleFixMessage quoteRequest = new SimpleFixMessage(MessageType.QuoteRequest);
        String quoteRequestId = randomAscii(3);
        quoteRequest.add(131, quoteRequestId); //quoteReqId
        String clientReqId = randomAscii(4);
        quoteRequest.add(11, clientReqId);


        Group instrument1 = quoteRequest.newGroup(146);//noRelatedSym
        instrument1.add(55, "EUR/USD");
        instrument1.add(167, "CURRENCY");

        Group instrument2 = quoteRequest.newGroup(146);//noRelatedSym
        instrument2.add(55, "EUR/CHF");
        instrument2.add(167, "CURRENCY");

        quoteRequest.add(303, 2); //QuoteRequestType=AUTOMATIC

        // read

        List<Group> instruments = quoteRequest.getGroups(146);
        assertEquals(2, instruments.size());

        assertSame(instrument1, instruments.get(0));
        assertSame(instrument2, instruments.get(1));
    }
}
