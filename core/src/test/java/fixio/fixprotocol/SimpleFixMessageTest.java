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

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.assertEquals;

public class SimpleFixMessageTest {

    private SimpleFixMessage fixMessage;

    @Before
    public void setUp() throws Exception {
        fixMessage = new SimpleFixMessage();
    }

    @Test
    public void headerFields() {
        String beginString = randomAlphanumeric(5);
        String senderCompID = randomAlphanumeric(5);
        String targetCompID = randomAlphanumeric(5);
        String msgType = randomAlphanumeric(5);

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
}
