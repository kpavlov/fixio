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

public abstract class AbstractAdminEvent implements AdminEvent {

    protected final FixSession session;

    public AbstractAdminEvent(FixSession session) {
        assert (session != null);
        this.session = session;
    }

    public FixSession getSession() {
        return session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractAdminEvent)) return false;
        final AbstractAdminEvent that = (AbstractAdminEvent) o;
        return session.equals(that.session);
    }

    @Override
    public int hashCode() {
        return session.hashCode();
    }
}
