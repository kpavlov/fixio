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

import fixio.events.LogoutEvent;
import fixio.fixprotocol.FieldType;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import fixio.fixprotocol.FixMessageBuilderImpl;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.session.FixSession;
import fixio.handlers.FixApplication;
import fixio.validator.BusinessRejectException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;

import java.util.List;

public abstract class AbstractSessionHandler extends MessageToMessageCodec<FixMessage, FixMessageBuilder> {

    public static final AttributeKey<FixSession> FIX_SESSION_KEY = AttributeKey.valueOf("fixSession");
    private final FixApplication fixApplication;
    private final FixClock fixClock;
    private final SessionRepository sessionRepository;


    protected AbstractSessionHandler(FixApplication fixApplication,
                                     FixClock fixClock,
                                     SessionRepository sessionRepository) {
        assert (fixApplication != null) : "FixApplication is required";
        assert (fixClock != null) : "Clock is required";
        this.fixApplication = fixApplication;
        this.fixClock = fixClock;
        this.sessionRepository = sessionRepository;
    }

    private static FixMessageBuilderImpl createReject(FixMessage originalMsg) {
        final FixMessageBuilderImpl reject = new FixMessageBuilderImpl(MessageTypes.REJECT);
        reject.add(FieldType.RefSeqNum, originalMsg.getInt(FieldType.MsgSeqNum.tag()));
        reject.add(FieldType.RefMsgType, originalMsg.getMessageType());
        return reject;
    }

    private static FixMessageBuilderImpl createBusinessReject(BusinessRejectException exception) {
        final FixMessageBuilderImpl reject = new FixMessageBuilderImpl(MessageTypes.BUSINESS_MESSAGE_REJECT);
        if (exception.getRefSeqNum() > 0) {
            reject.add(FieldType.RefSeqNum, exception.getRefSeqNum());
        }
        reject.add(FieldType.RefMsgType, exception.getRefMsgType());
        reject.add(FieldType.BusinessRejectReason, exception.getBusinessRejectReason());
        if (exception.getText() != null) {
            reject.add(FieldType.Text, exception.getText());
        }
        return reject;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Attribute<FixSession> fixSessionAttribute = ctx.channel().attr(FIX_SESSION_KEY);
        if (fixSessionAttribute != null) {
            FixSession session = fixSessionAttribute.getAndSet(null);
            if (session != null) {
                ctx.fireChannelRead(new LogoutEvent(session));
                sessionRepository.removeSession(session.getId());
                getLogger().info("Fix Session Closed. {}", session);
            }
        }
    }

    protected void prepareMessageToSend(ChannelHandlerContext ctx, FixMessageBuilder response) throws Exception {
        prepareMessageToSend(ctx, getSession(ctx), response);
    }

    protected void prepareMessageToSend(ChannelHandlerContext ctx, FixSession session, FixMessageBuilder response) throws Exception {
        session.prepareOutgoing(response);
        response.getHeader().setSendingTime(fixClock.millis());
        getFixApplication().beforeSendMessage(ctx, response);
    }

    /**
     * Retrieves {@link FixSession} from context.
     *
     * @return null if session not established.
     */
    protected FixSession getSession(ChannelHandlerContext ctx) {
        Attribute<FixSession> fixSessionAttribute = ctx.channel().attr(FIX_SESSION_KEY);
        return fixSessionAttribute.get();
    }

    protected boolean setSession(ChannelHandlerContext ctx, FixSession fixSession) {
        assert (fixSession != null) : "Parameter 'fixSession' expected.";
        Attribute<FixSession> fixSessionAttribute = ctx.channel().attr(FIX_SESSION_KEY);
        return fixSessionAttribute.compareAndSet(null, fixSession);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, FixMessageBuilder msg, List<Object> out) throws Exception {
        prepareMessageToSend(ctx, msg);
        getLogger().trace("Sending outbound: {}", msg);
        out.add(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        FixSession session = getSession(ctx);
        if (session == null) {
            getLogger().error("Session not established. Skipping message: {}", msg);
            ctx.channel().close();
            return;
        }

        FixMessageHeader header = msg.getHeader();

        final int msgSeqNum = header.getMsgSeqNum();
        if (!session.checkAndIncrementIncomingSeqNum(msgSeqNum)) {
            getLogger().error("MessageSeqNum={} != expected {}.", msgSeqNum, session.getNextIncomingMessageSeqNum());
        }
        out.add(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof BusinessRejectException) {
            FixMessageBuilderImpl businessMessageReject = createBusinessReject((BusinessRejectException) cause);
            ctx.channel().writeAndFlush(businessMessageReject);
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

    protected abstract Logger getLogger();

    protected void sendReject(ChannelHandlerContext ctx, FixMessage originalMsg, boolean closeConnection) {
        final FixMessageBuilderImpl reject = createReject(originalMsg);

        ChannelFuture channelFuture = ctx.writeAndFlush(reject);
        if (closeConnection) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    protected FixApplication getFixApplication() {
        return fixApplication;
    }

    protected SessionRepository getSessionRepository() {
        return sessionRepository;
    }

}
