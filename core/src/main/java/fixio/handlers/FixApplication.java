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

import java.util.List;

public interface FixApplication extends ChannelHandler {

    /**
     * Invoked after FIX session was successfully established.
     */
    void onLogon(ChannelHandlerContext ctx, LogonEvent msg);

    /**
     * Invoked after FIX session was closed.
     */
    void onLogout(ChannelHandlerContext ctx, LogoutEvent msg);

    /**
     * Invoked when message arrived
     */
    void onMessage(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception;

    /**
     * Invoked before {@link FixMessageBuilder} is sent.
     */
    void beforeSendMessage(ChannelHandlerContext ctx, FixMessageBuilder messageBuilder) throws Exception;
}
