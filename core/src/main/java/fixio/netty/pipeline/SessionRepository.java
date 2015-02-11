package fixio.netty.pipeline;

import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.session.FixSession;
import fixio.fixprotocol.session.SessionId;

public interface SessionRepository {

    FixSession createSession(FixMessageHeader header);

    FixSession getSession(FixMessageHeader header);

    void removeSession(SessionId sessionId);
}
