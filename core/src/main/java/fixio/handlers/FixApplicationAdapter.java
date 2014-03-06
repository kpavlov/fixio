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
package fixio.handlers;

import fixio.events.LogonEvent;
import fixio.events.LogoutEvent;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ChannelHandler.Sharable
public class FixApplicationAdapter extends MessageToMessageDecoder<Object> implements FixApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixApplicationAdapter.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        try {
            if (msg instanceof FixMessage) {
                onMessage(ctx, (FixMessage) msg, out);
            } else if (msg instanceof LogonEvent) {
                onLogon(ctx, (LogonEvent) msg);
            } else if (msg instanceof LogoutEvent) {
                onLogout(ctx, (LogoutEvent) msg);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void onLogon(ChannelHandlerContext ctx, LogonEvent msg) {
    }

    @Override
    public void onLogout(ChannelHandlerContext ctx, LogoutEvent msg) {
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
    }

    @Override
    public void beforeSendMessage(ChannelHandlerContext ctx, FixMessageBuilder msg) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Uncaught application exception.", cause);
        ctx.close().sync();
    }
}
