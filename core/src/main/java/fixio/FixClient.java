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

import fixio.fixprotocol.FixMessage;
import fixio.handlers.AdminEventHandler;
import fixio.handlers.FixApplicationAdapter;
import fixio.handlers.FixMessageHandler;
import fixio.netty.pipeline.client.FixInitiatorChannelInitializer;
import fixio.netty.pipeline.client.PropertyFixSessionSettingsProviderImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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

    private String settingsResource;

    public FixClient(FixApplicationAdapter fixApplication) {
        this(fixApplication, fixApplication);
    }

    public FixClient(AdminEventHandler adminEventHandler, FixMessageHandler... appMessageHandlers) {
        super(adminEventHandler, appMessageHandlers);
        settingsResource = "/fixClient.properties";
    }

    public void setSettingsResource(String settingsResource) {
        this.settingsResource = settingsResource;
    }

    public ChannelFuture connect(int port) throws InterruptedException {
        return connect(new InetSocketAddress(port));
    }

    public ChannelFuture connect(SocketAddress serverAddress) throws InterruptedException {
        Bootstrap b = new Bootstrap();
        try {
            b.group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .remoteAddress(serverAddress)
                    .handler(new FixInitiatorChannelInitializer<SocketChannel>(
                            new PropertyFixSessionSettingsProviderImpl(settingsResource),
                            getAdminHandler(),
                            getAppMessageHandlers()
                    ))
                    .validate();

            channel = b.connect().sync().channel();
            LOGGER.info(FixClient.class.getName() + " started and connected to " + channel.remoteAddress());
            return channel.closeFuture();
        } finally {
            // b.shutdown();
        }
    }

    public void disconnect() throws InterruptedException {
        LOGGER.info("Closing connection to {}", channel.remoteAddress());
        channel.close().sync();

    }

    public void send(FixMessage fixMessage) throws InterruptedException {
        channel.writeAndFlush(fixMessage);
    }
}
