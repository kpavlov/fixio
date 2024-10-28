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
import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(fixMessage.getHeader().getBeginString()).as("beginString").isEqualTo(beginString);
        assertThat(fixMessage.getMessageType()).as("msgType").isEqualTo(msgType);
        assertThat(fixMessage.getHeader().getSenderCompID()).as("senderCompID").isEqualTo(senderCompID);
        assertThat(fixMessage.getHeader().getTargetCompID()).as("targetCompID").isEqualTo(targetCompID);
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
        assertThat(instruments.size()).isEqualTo(2);

        assertThat(instruments.get(0)).isSameAs(instrument1);
        assertThat(instruments.get(1)).isSameAs(instrument2);
    }

    @Test
    void addStringByTypeAndTag() {
        String value = randomAscii(3);
        int tagNum = new Random().nextInt(100) + 100;

        FixMessageBuilderImpl messageBuilder = new FixMessageBuilderImpl();
        assertThat(messageBuilder.add(DataType.STRING, tagNum, value)).isSameAs(messageBuilder);

        assertThat(messageBuilder.getString(tagNum)).isEqualTo(value);
    }

    @Test
    void addIntByTypeAndTag() {
        int value = RANDOM.nextInt(1000);
        int tagNum = RANDOM.nextInt(100) + 100;

        FixMessageBuilderImpl messageBuilder = new FixMessageBuilderImpl();
        assertThat(messageBuilder.add(DataType.LENGTH, tagNum, value)).isSameAs(messageBuilder);

        assertThat(messageBuilder.getInt(tagNum)).isEqualTo((Integer) value);
    }

    @Test
    void addIntByTag() {
        int value = RANDOM.nextInt(1000);
        FieldType tag = FieldType.MsgSeqNum;

        FixMessageBuilderImpl messageBuilder = new FixMessageBuilderImpl();
        assertThat(messageBuilder.add(tag, value)).isSameAs(messageBuilder);

        assertThat(messageBuilder.getInt(tag)).isEqualTo((Integer) value);
    }
}
