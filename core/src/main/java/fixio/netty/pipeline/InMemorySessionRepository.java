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

import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionRepository implements SessionRepository {

    private final ConcurrentHashMap<SessionId, FixSession> sessions = new ConcurrentHashMap<>();

    private static SessionId createSessionId(FixMessageHeader header) {
        return new SessionId(
                header.getSenderCompID(),
                header.getTargetCompID(),
                header.getSenderSubID(),
                header.getTargetSubID(),
                header.getSenderLocationID(),
                header.getTargetLocationID()
        );
    }

    @Override
    public FixSession getOrCreateSession(FixMessageHeader header) {
        SessionId id = createSessionId(header);

        FixSession newSession = FixSession.newBuilder()
                .beginString(header.getBeginString())
                .senderCompId(header.getSenderCompID())
                .senderSubId(header.getSenderSubID())
                .senderLocationID(header.getSenderLocationID())
                .targetCompId(header.getTargetCompID())
                .targetSubId(header.getTargetSubID())
                .targetLocationID(header.getTargetLocationID())
                .build();

        newSession.setNextIncomingMessageSeqNum(1);
        newSession.setNextOutgoingMessageSeqNum(1);

        final FixSession existingSession = sessions.putIfAbsent(id, newSession);
        return existingSession == null ? newSession : existingSession;

    }

    /**
     * Returns existing {@link FixSession} from the repository, or null if no such session is present.
     *
     * @param header FixMessageHeader containing session information
     * @return null if no session found in repository
     */
    @Override
    public FixSession getSession(FixMessageHeader header) {
        SessionId id = createSessionId(header);

        return sessions.get(id);
    }

    @Override
    public void removeSession(SessionId sessionId) {
        sessions.remove(sessionId);
    }
}
