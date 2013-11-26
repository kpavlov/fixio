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

import fixio.fixprotocol.FieldType;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.SimpleFixMessage;
import fixio.handlers.FixMessageHandlerAdapter;
import fixio.netty.pipeline.server.FixAcceptorChannelInitializer;
import fixio.netty.pipeline.server.FixAuthenticator;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerChannelPipelineIntegrationTest {

    @Mock
    private FixAuthenticator authenticator;
    private LocalServerChannel serverChannel;
    private ChannelPipeline pipeline;
    //    private LocalChannel clientChannel;
//    private Channel channel;

    @Before
    public void setUp() throws Exception {
        ServerBootstrap b = new ServerBootstrap();

//        b.channel(serverChannel)


        LocalAddress address = LocalAddress.ANY;

//        address = serverChannel.localAddress();

        EventLoopGroup workerGroup = new LocalEventLoopGroup();
//        channel = new EmbeddedChannel(new ChannelOutboundHandlerAdapter());
        final FixAcceptorChannelInitializer<Channel> channelInitializer = new FixAcceptorChannelInitializer<>(workerGroup, null);
        channelInitializer.setAuthenticator(authenticator);

        serverChannel = (LocalServerChannel) b.group(new LocalEventLoopGroup())
                .channel(LocalServerChannel.class)
                .handler(channelInitializer)
                .childHandler(new FixMessageHandlerAdapter())
                .validate()
                .bind(address)
                .sync()
                .channel();

        pipeline = serverChannel.pipeline();

        when(authenticator.authenticate(any(FixMessageHeader.class))).thenReturn(true);
    }

    @After
    public void tearDown() throws InterruptedException {
        serverChannel.close().sync();
    }

    @Test
    public void processLogonSuccess() {
        final SimpleFixMessage logon = new SimpleFixMessage(MessageTypes.LOGON);

        pipeline.fireChannelRead(logon);
        pipeline.flush();
//        pipeline.fireChannelRead(logon);

    }

    @Test
    public void processHeartbeat() {
        pipeline.flush();
        //pipeline.fireChannelActive();
        //serverChannel.eventLoop();

//        channel.writeInbound(new SimpleFixMessage(MessageTypes.LOGON));
        final SimpleFixMessage testRequest = new SimpleFixMessage(MessageTypes.TEST_REQUEST);
        testRequest.add(FieldType.TestReqID, randomAlphanumeric(5));

        pipeline.fireChannelRead(testRequest);
//        pipeline.fireChannelReadComplete();
        pipeline.flush();

//        pipeline.flush();
//        serverChannel.writeInbound(testRequest);

//        final Object o = channel.readOutbound();

    }
}
