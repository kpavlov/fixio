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

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.assertj.core.api.Assertions.assertThat;

class FixMessageImplTest {

    private FixMessageImpl fixMessage;

    @BeforeEach
    void setUp() {
        fixMessage = new FixMessageImpl();
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

        //FixMessageImpl fixMessage = builder.build();

        assertThat(fixMessage.getHeader().getBeginString()).as("beginString").isEqualTo(beginString);
        assertThat(fixMessage.getMessageType()).as("msgType").isEqualTo(msgType);
        assertThat(fixMessage.getHeader().getSenderCompID()).as("senderCompID").isEqualTo(senderCompID);
        assertThat(fixMessage.getHeader().getTargetCompID()).as("targetCompID").isEqualTo(targetCompID);
    }


}
