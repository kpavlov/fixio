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

package fixio.netty.pipeline.server;

import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import fixio.handlers.FixApplication;
import fixio.netty.pipeline.FixChannelInitializer;
import fixio.netty.pipeline.SessionRepository;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.MessageToMessageCodec;

public class FixAcceptorChannelInitializer<C extends Channel> extends FixChannelInitializer<C> {

    private final FixAuthenticator authenticator;
    private final SessionRepository sessionRepository;

    public FixAcceptorChannelInitializer(EventLoopGroup workerGroup,
                                         FixApplication fixApplication,
                                         FixAuthenticator authenticator,
                                         SessionRepository sessionRepository) {
        super(workerGroup, fixApplication, false);
        this.authenticator = authenticator;
        this.sessionRepository = sessionRepository;
    }

    @Override
    protected MessageToMessageCodec<FixMessage, FixMessageBuilder> createSessionHandler() {
        return new ServerSessionHandler(getFixApplication(), authenticator, sessionRepository);
    }

}


