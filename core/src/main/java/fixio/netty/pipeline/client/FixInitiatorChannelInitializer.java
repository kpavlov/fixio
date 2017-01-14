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

package fixio.netty.pipeline.client;

import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import fixio.handlers.FixApplication;
import fixio.netty.pipeline.FixChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.MessageToMessageCodec;

public class FixInitiatorChannelInitializer<C extends Channel> extends FixChannelInitializer<C> {
    private final MessageSequenceProvider messageSequenceProvider;
    private final FixSessionSettingsProvider settingsProvider;
    private final AuthenticationProvider authenticationProvider;

    public FixInitiatorChannelInitializer(EventLoopGroup workerGroup,
                                          FixSessionSettingsProvider settingsProvider,
                                          AuthenticationProvider authenticationProvider,
                                          MessageSequenceProvider messageSequenceProvider,
                                          FixApplication fixApplication) {
        super(workerGroup, fixApplication);
        this.authenticationProvider = authenticationProvider;
        this.messageSequenceProvider = (messageSequenceProvider == null) ? StatelessMessageSequenceProvider.getInstance() : messageSequenceProvider;
        this.settingsProvider = settingsProvider;
    }

    @Override
    protected MessageToMessageCodec<FixMessage, FixMessageBuilder> createSessionHandler() {
        return new ClientSessionHandler(settingsProvider, authenticationProvider, messageSequenceProvider, getFixApplication());
    }
}
