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
package fixio.examples.priceclient;

import fixio.FixClient;
import fixio.examples.priceserver.PriceServer;
import fixio.netty.pipeline.InMemorySessionRepository;
import fixio.netty.pipeline.client.PropertyFixSessionSettingsProviderImpl;
import fixio.netty.pipeline.client.StatelessMessageSequenceProvider;
import io.netty.channel.ChannelFuture;

public class PriceClient {

    private final FixClient client;
    private final int port;

    public PriceClient(int port) {
        PropertyFixSessionSettingsProviderImpl propertyImpl = new PropertyFixSessionSettingsProviderImpl("/client.properties");
        this.port = port;
        final PriceReadingApp app = new PriceReadingApp();
        client = new FixClient(app, new InMemorySessionRepository());
        client.setSessionSettingsProvider(propertyImpl);
        client.setMessageSequenceProvider(StatelessMessageSequenceProvider.getInstance());
    }

    public static void main(String[] args) throws InterruptedException {
        PriceClient priceClient = new PriceClient(PriceServer.DEFAULT_PORT);
        ChannelFuture channelFuture = priceClient.connect();
        channelFuture.sync();
        priceClient.shutdown();
    }

    public ChannelFuture connect() throws InterruptedException {
        return client.connect("localhost", port);
    }

    public void shutdown() throws InterruptedException {
        client.disconnect();
    }
}
