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
package fixio.fixprotocol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class FixMessageBuilderImplTest {

    private static final Random RANDOM = new Random();

    private FixMessageBuilderImpl fixMessage;

    @BeforeEach
    void setUp() {
        fixMessage = new FixMessageBuilderImpl();
    }

    @Test
    void headerFields() {
        String beginString = randomAscii(2);
        String senderCompID = randomAscii(3);
        String targetCompID = randomAscii(4);
        String msgType = randomAscii(5);

        fixMessage.getHeader().setBeginString(beginString);
        fixMessage.setMessageType(msgType);
        fixMessage.getHeader().setSenderCompID(senderCompID);
        fixMessage.getHeader().setTargetCompID(targetCompID);

        assertEquals(beginString, fixMessage.getHeader().getBeginString(), "beginString");
        assertEquals(msgType, fixMessage.getMessageType(), "msgType");
        assertEquals(senderCompID, fixMessage.getHeader().getSenderCompID(), "senderCompID");
        assertEquals(targetCompID, fixMessage.getHeader().getTargetCompID(), "targetCompID");
    }

    @Test
    void withGroups() {
        String clientOrderId = randomAscii(4);
        String quoteRequestId = randomAscii(3);

        FixMessageBuilderImpl quoteRequest = new FixMessageBuilderImpl(MessageTypes.QUOTE_REQUEST);
        quoteRequest.add(FieldType.QuoteReqID, quoteRequestId);
        quoteRequest.add(FieldType.ClOrdID, clientOrderId);

        Group instrument1 = quoteRequest.newGroup(FieldType.NoRelatedSym, 2);
        instrument1.add(FieldType.Symbol, "EUR/USD");
        instrument1.add(FieldType.SecurityType, "CURRENCY");

        Group instrument2 = quoteRequest.newGroup(FieldType.NoRelatedSym);
        instrument2.add(FieldType.Symbol, "EUR/CHF");
        instrument2.add(FieldType.SecurityType, "CURRENCY");

        quoteRequest.add(FieldType.QuoteRequestType, 2); //QuoteRequestType=AUTOMATIC

        // read

        List<Group> instruments = quoteRequest.getValue(FieldType.NoRelatedSym);
        assertEquals(2, instruments.size());

        assertSame(instrument1, instruments.get(0));
        assertSame(instrument2, instruments.get(1));
    }

    @Test
    void addStringByTypeAndTag() {
        String value = randomAscii(3);
        int tagNum = new Random().nextInt(100) + 100;

        FixMessageBuilderImpl messageBuilder = new FixMessageBuilderImpl();
        assertSame(messageBuilder, messageBuilder.add(DataType.STRING, tagNum, value));

        assertEquals(value, messageBuilder.getString(tagNum));
    }

    @Test
    void addIntByTypeAndTag() {
        int value = RANDOM.nextInt(1000);
        int tagNum = RANDOM.nextInt(100) + 100;

        FixMessageBuilderImpl messageBuilder = new FixMessageBuilderImpl();
        assertSame(messageBuilder, messageBuilder.add(DataType.LENGTH, tagNum, value));

        assertEquals((Integer) value, messageBuilder.getInt(tagNum));
    }

    @Test
    void addIntByTag() {
        int value = RANDOM.nextInt(1000);
        FieldType tag = FieldType.MsgSeqNum;

        FixMessageBuilderImpl messageBuilder = new FixMessageBuilderImpl();
        assertSame(messageBuilder, messageBuilder.add(tag, value));

        assertEquals((Integer) value, messageBuilder.getInt(tag));
    }
}
