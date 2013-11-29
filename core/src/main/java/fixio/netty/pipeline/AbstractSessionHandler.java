/*
 * Copyright 2013 The FIX.io Project
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

import fixio.fixprotocol.*;
import fixio.fixprotocol.session.FixSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractSessionHandler extends MessageToMessageCodec<FixMessage, FixMessage> {

    protected AtomicReference<FixSession> sessionHolder = new AtomicReference<>();

    protected void updateFixMessageHeader(FixMessage response) {
        sessionHolder.get().prepareOutgoing(response);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        sessionHolder.set(null);
        getLogger().info("Fix Session Closed.");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        updateFixMessageHeader(msg);
        getLogger().debug("Sending outbound: {}", msg);
        out.add(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        if (sessionHolder.get() == null) {
            getLogger().error("Session not established. Skipping message: {}", msg);
            ctx.channel().closeFuture().await();
        }

        FixMessageHeader header = msg.getHeader();

        FixSession session = SessionRepository.getInstance().getSession(header);
        final int msgSeqNum = header.getMsgSeqNum();
        if (!session.checkIncomingSeqNum(msgSeqNum)) {
            getLogger().error("MessageSeqNum={} != expected {}.", msgSeqNum, session.getNextIncomingMessageSeqNum());
        }
        out.add(msg);
    }

    protected abstract Logger getLogger();

    protected void sendReject(ChannelHandlerContext ctx, FixMessage originalMsg, boolean closeConnection) {
        final SimpleFixMessage reject = createReject(originalMsg);

        ctx.writeAndFlush(reject);
    }

    protected SimpleFixMessage createReject(FixMessage originalMsg) {
        final SimpleFixMessage reject = new SimpleFixMessage(MessageTypes.REJECT);
        reject.add(FieldType.RefSeqNum, originalMsg.getInt(FieldType.MsgSeqNum.tag()));
        reject.add(FieldType.RefMsgType, originalMsg.getMessageType());
        return reject;
    }

}
