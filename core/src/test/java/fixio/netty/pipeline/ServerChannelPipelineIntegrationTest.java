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

import fixio.fixprotocol.FieldType;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilderImpl;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.MessageTypes;
import fixio.handlers.FixApplicationAdapter;
import fixio.netty.pipeline.server.FixAcceptorChannelInitializer;
import fixio.netty.pipeline.server.FixAuthenticator;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServerChannelPipelineIntegrationTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private FixAuthenticator authenticator;
    private LocalServerChannel serverChannel;
    private ChannelPipeline pipeline;

    @BeforeEach
    void setUp() throws Exception {
        ServerBootstrap b = new ServerBootstrap();

        LocalAddress address = LocalAddress.ANY;

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final FixAcceptorChannelInitializer<Channel> channelInitializer = new FixAcceptorChannelInitializer<>(
                workerGroup,
                new FixApplicationAdapter(),
                authenticator,
                new InMemorySessionRepository()
        );

        serverChannel = (LocalServerChannel) b.group(new NioEventLoopGroup())
                .channel(LocalServerChannel.class)
                .handler(channelInitializer)
                .childHandler(new FixApplicationAdapter())
                .validate()
                .bind(address)
                .sync()
                .channel();

        pipeline = serverChannel.pipeline();

        when(authenticator.authenticate(any(FixMessage.class))).thenReturn(true);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        serverChannel.close().sync();
    }

    @Test
    void processLogonSuccess() {
        final FixMessageBuilderImpl logon = new FixMessageBuilderImpl(MessageTypes.LOGON);
        final FixMessageHeader header = logon.getHeader();
        header.setSenderCompID(randomAscii(3));
        header.setTargetCompID(randomAscii(4));

        pipeline.fireChannelRead(logon);
        pipeline.flush();
    }

    @Test
    void processHeartbeat() {
        final FixMessageBuilderImpl testRequest = new FixMessageBuilderImpl(MessageTypes.TEST_REQUEST);
        final FixMessageHeader header = testRequest.getHeader();
        header.setSenderCompID(randomAscii(3));
        header.setTargetCompID(randomAscii(4));

        testRequest.add(FieldType.TestReqID, randomAscii(5));



        pipeline.fireChannelRead(testRequest);
        pipeline.flush();
    }
}
