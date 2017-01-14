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

import fixio.handlers.FixApplication;
import fixio.netty.pipeline.SessionRepository;
import fixio.netty.pipeline.server.FixAcceptorChannelInitializer;
import fixio.netty.pipeline.server.FixAuthenticator;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class FixServer extends AbstractFixConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixServer.class);
    private final int port;
    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private FixAuthenticator authenticator;

    public FixServer(int port,
                     FixApplication fixApplication,
                     FixAuthenticator authenticator,
                     SessionRepository sessionRepository) {
        super(fixApplication, sessionRepository);
        assert (authenticator != null) : "Authenticator is required";
        this.authenticator = authenticator;
        this.port = port;
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        final ServerBootstrap bootstrap = new ServerBootstrap();
        final FixAcceptorChannelInitializer<SocketChannel> channelInitializer = new FixAcceptorChannelInitializer<>(
                workerGroup,
                getFixApplication(),
                authenticator,
                getSessionRepository()
        );


        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY,
                        Boolean.parseBoolean(System.getProperty(
                                "nfs.rpc.tcp.nodelay", "true")))
                .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator())
                .localAddress(new InetSocketAddress(port))
                .childHandler(channelInitializer)
                .validate();

        ChannelFuture f = bootstrap.bind().sync();
        LOGGER.info("FixServer is started at {}", f.channel().localAddress());
        channel = f.channel();
    }

    public void stop() {
        if (channel == null) {
            throw new IllegalStateException("Server is not started.");
        }
        LOGGER.info("Stopping FixServer");
        try {
            channel.close().sync();
            channel = null;
        } catch (InterruptedException e) {
            LOGGER.error("Error while stopping server", e);
        } finally {
            bossGroup.shutdownGracefully();
            bossGroup = null;
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
    }
}
