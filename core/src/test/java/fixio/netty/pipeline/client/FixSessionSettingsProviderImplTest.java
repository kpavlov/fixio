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
package fixio.netty.pipeline.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FixSessionSettingsProviderImplTest {

    private static FixSessionSettingsProviderImpl settingsProvider;

    private static String beginString;
    private static String senderCompID;
    private static String senderSubID;
    private static String targetCompID;
    private static String targetSubID;
    private static boolean resetMsgSeqNum;
    private static int heartbeatInterval;

    @BeforeAll
    static void setUp() {
        beginString = randomAscii(5);
        senderCompID = randomAscii(5);
        senderSubID = randomAscii(5);
        targetCompID = randomAscii(5);
        targetSubID = randomAscii(5);
        resetMsgSeqNum = true;
        heartbeatInterval = new Random().nextInt(100) + 1;


        settingsProvider = FixSessionSettingsProviderImpl.newBuilder()
                .beginString(beginString)
                .senderCompID(senderCompID)
                .senderSubID(senderSubID)
                .targetCompID(targetCompID)
                .targetSubID(targetSubID)
                .resetMsgSeqNum(resetMsgSeqNum)
                .heartbeatInterval(heartbeatInterval)
                .build();
    }

    @Test
    void getSenderCompID() {
        assertEquals(senderCompID, settingsProvider.getSenderCompID());
    }

    @Test
    void getSenderSubID() {
        assertEquals(senderSubID, settingsProvider.getSenderSubID());
    }

    @Test
    void getTargetCompID() {
        assertEquals(targetCompID, settingsProvider.getTargetCompID());
    }

    @Test
    void getTargetSubID() {
        assertEquals(targetSubID, settingsProvider.getTargetSubID());
    }

    @Test
    void getBeginString() {
        assertEquals(beginString, settingsProvider.getBeginString());
    }

    @Test
    void isResetMsgSeqNum() {
        assertEquals(resetMsgSeqNum, settingsProvider.isResetMsgSeqNum());
    }

    @Test
    void getHeartbeatInterval() {
        assertEquals(heartbeatInterval, settingsProvider.getHeartbeatInterval());
    }
}
