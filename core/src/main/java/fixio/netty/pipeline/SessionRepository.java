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

public class SessionRepository {

    private static final SessionRepository INSTANCE = new SessionRepository();

    private final ConcurrentHashMap<SessionId, FixSession> sessions = new ConcurrentHashMap<>();

    public static SessionRepository getInstance() {
        return INSTANCE;
    }

    private SessionRepository() {
    }

    public FixSession createSession(FixMessageHeader header) {
        SessionId id = createSessionId(header);

        FixSession session = FixSession.newBuilder()
                .beginString(header.getBeginString())
                .senderCompId(header.getSenderCompID())
                .senderSubId(header.getSenderSubID())
                .targetCompId(header.getTargetCompID())
                .targetSubId(header.getTargetSubID())
                .build();

        session.setNextIncomingMessageSeqNum(1);
        session.setNextOutgoingMessageSeqNum(1);

        sessions.put(id, session);
        return session;
    }

    /**
     * Returns existing {@link FixSession} from the repository, or null if no such session is present.
     *
     * @param header FixMessageHeader containing session information
     * @return null if no session found in repository
     */
    public FixSession getSession(FixMessageHeader header) {
        SessionId id = createSessionId(header);

        return sessions.get(id);
    }

    private static SessionId createSessionId(FixMessageHeader header) {
        return new SessionId(
                header.getSenderCompID(),
                header.getTargetCompID(),
                header.getSenderSubID(),
                header.getTargetSubID()
        );
    }
}
