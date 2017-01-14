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

package fixio;

import fixio.fixprotocol.FixMessageBuilder;
import fixio.handlers.FixApplication;
import fixio.netty.pipeline.SessionRepository;
import fixio.netty.pipeline.client.AuthenticationProvider;
import fixio.netty.pipeline.client.FixInitiatorChannelInitializer;
import fixio.netty.pipeline.client.FixSessionSettingsProvider;
import fixio.netty.pipeline.client.MessageSequenceProvider;
import fixio.netty.pipeline.client.PropertyFixSessionSettingsProviderImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class FixClient extends AbstractFixConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixClient.class);
    private Channel channel;
    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;
    private FixSessionSettingsProvider sessionSettingsProvider;
    private MessageSequenceProvider messageSequenceProvider;
    private AuthenticationProvider authenticationProvider;

    public FixClient(FixApplication fixApplication,
                     SessionRepository sessionRepository) {
        super(fixApplication, sessionRepository);
    }

    /**
     * Initialize {@link #sessionSettingsProvider} with {@link PropertyFixSessionSettingsProviderImpl}
     * using specified property file resource.
     *
     * @param settingsResource property file location related to classpath.
     */
    public void setSettingsResource(String settingsResource) {
        this.sessionSettingsProvider = new PropertyFixSessionSettingsProviderImpl(settingsResource);
    }

    public void setSessionSettingsProvider(FixSessionSettingsProvider sessionSettingsProvider) {
        assert sessionSettingsProvider != null;
        this.sessionSettingsProvider = sessionSettingsProvider;
    }

    public void setMessageSequenceProvider(MessageSequenceProvider messageSequenceProvider) {
        this.messageSequenceProvider = messageSequenceProvider;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    /**
     * Connect and start FIX session to specified host and port.
     */
    public ChannelFuture connect(String host, int port) throws InterruptedException {
        return connect(new InetSocketAddress(host, port));
    }

    public ChannelFuture connect(SocketAddress serverAddress) throws InterruptedException {
        final Channel channel = connectAsync(serverAddress).sync().await().channel();
        assert (channel != null) : "Channel must be set";
        LOGGER.info("FixClient is started and connected to {}", channel.remoteAddress());
        return channel.closeFuture();
    }

    public ChannelFuture connectAsync(SocketAddress serverAddress) {
        LOGGER.info("FixClient is starting");
        final Bootstrap b = new Bootstrap();
        bossEventLoopGroup = new NioEventLoopGroup();
        workerEventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        b.group(bossEventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(serverAddress)
                .option(ChannelOption.TCP_NODELAY,
                        Boolean.parseBoolean(System.getProperty(
                                "nfs.rpc.tcp.nodelay", "true")))
                .option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator())
                .handler(new FixInitiatorChannelInitializer<SocketChannel>(
                        workerEventLoopGroup,
                        sessionSettingsProvider,
                        authenticationProvider,
                        messageSequenceProvider,
                        getFixApplication()
                ))
                .validate();

        final ChannelFuture connectFuture = b.connect();
        return connectFuture.addListener(future -> channel = connectFuture.channel());
    }

    public ChannelFuture disconnectAsync() {
        LOGGER.info("Closing connection to {}", channel.remoteAddress());
        return channel.close().addListener(future -> {
            if (workerEventLoopGroup != null)
                workerEventLoopGroup.shutdownGracefully();
            if (bossEventLoopGroup != null)
                bossEventLoopGroup.shutdownGracefully();
            bossEventLoopGroup = null;
            workerEventLoopGroup = null;
            LOGGER.info("Connection to {} was closed.", channel.remoteAddress());
        });
    }

    public void disconnect() throws InterruptedException {
        disconnectAsync().await();
    }

    public void send(FixMessageBuilder fixMessageBuilder) {
        channel.writeAndFlush(fixMessageBuilder);
    }

}
