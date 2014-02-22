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
import io.netty.channel.ChannelFuture;

public class PriceClient {

    private final PriceReadingApp app;
    private final FixClient client;
    private final int port;

    public PriceClient(int port) {
        this.port = port;
        app = new PriceReadingApp();
        client = new FixClient(app);
        client.setSettingsResource("/client.properties");
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
