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

package fixio;

import fixio.handlers.AdminEventHandler;
import fixio.handlers.FixMessageHandler;
import fixio.netty.pipeline.server.FixAcceptorChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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

    public FixServer(int port, FixMessageHandler... appMessageHandlers) {
        this(port, null, appMessageHandlers);
    }

    public FixServer(int port, AdminEventHandler adminEventHandler, FixMessageHandler... appMessageHandlers) {
        super(adminEventHandler, appMessageHandlers);
        this.port = port;
    }

    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new FixAcceptorChannelInitializer<SocketChannel>(
                        getAdminHandler(),
                        getAppMessageHandlers()
                ))
                .validate();

        ChannelFuture f = bootstrap.bind().sync();
        System.out.println(FixServer.class.getName() +
                " started and listen on " + f.channel().localAddress());
        channel = f.channel();
    }

    public void stop() {
        LOGGER.info("Stopping FixServer");
        try {
            channel.close().sync();
            channel = null;
        } catch (InterruptedException e) {
            LOGGER.error("Error while stopping server", e);
        }
    }
}
