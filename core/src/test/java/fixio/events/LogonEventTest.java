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
package fixio.events;

import fixio.fixprotocol.session.FixSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class LogonEventTest {

    @Mock
    private FixSession session1;
    @Mock
    private FixSession session2;

    @Test
    void getSession() {
        assertSame(session1, new LogonEvent(session1).getSession());
    }

    @Test
    void equalsSameSession() {
        assertEquals(new LogonEvent(session1), new LogonEvent(session1));
    }

    @Test
    void equalsSelf() {
        LogonEvent LogonEvent = new LogonEvent(session1);
        assertEquals(LogonEvent, LogonEvent);
    }

    @Test
    void notEqualsDifferentSession() {
        LogonEvent firstEvent = new LogonEvent(session1);
        LogonEvent secondEvent = new LogonEvent(session2);

        assertNotEquals(firstEvent, secondEvent);
        assertNotEquals(secondEvent, firstEvent);
    }

    @Test
    void hashCodeIsSameForSelf() {
        LogonEvent LogonEvent = new LogonEvent(session1);
        assertEquals(LogonEvent.hashCode(), LogonEvent.hashCode());
    }

    @Test
    void hashCodeIsSameForSameSession() {
        assertEquals(new LogonEvent(session1).hashCode(), new LogonEvent(session1).hashCode());
    }

    @Test
    void hashCodeIsDifferentForSameSessionDifferentSession() {
        LogonEvent firstEvent = new LogonEvent(session1);
        LogonEvent secondEvent = new LogonEvent(session2);

        assertNotEquals(firstEvent.hashCode(), secondEvent.hashCode());
    }

}
