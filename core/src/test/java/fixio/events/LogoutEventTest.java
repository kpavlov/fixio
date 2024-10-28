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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LogoutEventTest {

    @Mock
    private FixSession session1;
    @Mock
    private FixSession session2;

    @Test
    void getSession() {
        assertThat(new LogoutEvent(session1).getSession()).isSameAs(session1);
    }

    @Test
    void equalsSameSession() {
        assertThat(new LogoutEvent(session1)).isEqualTo(new LogoutEvent(session1));
    }

    @Test
    void equalsSelf() {
        LogoutEvent logoutEvent = new LogoutEvent(session1);
        assertThat(logoutEvent).isEqualTo(logoutEvent);
    }

    @Test
    void notEqualsDifferentSession() {
        LogoutEvent firstEvent = new LogoutEvent(session1);
        LogoutEvent secondEvent = new LogoutEvent(session2);

        assertThat(secondEvent).isNotEqualTo(firstEvent);
        assertThat(firstEvent).isNotEqualTo(secondEvent);
    }

    @Test
    void hashCodeIsSameForSelf() {
        LogoutEvent logoutEvent = new LogoutEvent(session1);
        assertThat(logoutEvent.hashCode()).isEqualTo(logoutEvent.hashCode());
    }

    @Test
    void hashCodeIsSameForSameSession() {
        assertThat(new LogoutEvent(session1).hashCode()).isEqualTo(new LogoutEvent(session1).hashCode());
    }

    @Test
    void hashCodeIsDifferentForSameSessionDifferentSession() {
        LogoutEvent firstEvent = new LogoutEvent(session1);
        LogoutEvent secondEvent = new LogoutEvent(session2);

        assertThat(secondEvent.hashCode()).isNotEqualTo(firstEvent.hashCode());
    }

}
