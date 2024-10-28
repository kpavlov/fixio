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

import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.session.FixSession;
import fixio.fixprotocol.session.SessionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class InMemorySessionRepositoryTest {

    private SessionRepository sessionRepository;
    private String senderCompID;
    private String senderSubID;
    private String targetCompID;
    private String targetSubID;
    private FixMessageHeader header;

    @BeforeEach
    void setUp() {
        senderCompID = randomAscii(2);
        senderSubID = randomAscii(3);
        targetCompID = randomAscii(4);
        targetSubID = randomAscii(5);

        header = new FixMessageHeader();
        header.setSenderCompID(senderCompID);
        header.setSenderSubID(senderSubID);
        header.setTargetCompID(targetCompID);
        header.setTargetSubID(targetSubID);

        sessionRepository = new InMemorySessionRepository();
    }

    @Test
    void normalLifecycle() {

        FixSession session = sessionRepository.getOrCreateSession(header);

        assertNotNull(session);

        FixSession readSession = sessionRepository.getSession(header);

        assertSame(session, readSession, "not same.");

        assertSame(senderCompID, session.getSenderCompID());
        assertSame(senderSubID, session.getSenderSubID());
        assertSame(targetCompID, session.getTargetCompID());
        assertSame(targetSubID, session.getTargetSubID());

        final SessionId sessionId = session.getId();

        sessionRepository.removeSession(sessionId);

        assertNull(sessionRepository.getSession(header), "Session was not destroyed.");
    }
}
