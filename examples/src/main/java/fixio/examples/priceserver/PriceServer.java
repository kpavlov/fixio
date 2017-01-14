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
package fixio.examples.priceserver;

import fixio.FixServer;
import fixio.examples.common.Quote;
import fixio.examples.common.QuoteGeneratorTask;
import fixio.netty.pipeline.InMemorySessionRepository;
import fixio.netty.pipeline.server.AcceptAllAuthenticator;

import java.util.concurrent.ArrayBlockingQueue;

public class PriceServer {

    public static final int DEFAULT_PORT = 10101;

    private final ArrayBlockingQueue<Quote> quoteQueue = new ArrayBlockingQueue<>(8192);

    private final FixServer server;
    private Thread generator;
    private QuoteGeneratorTask generatorTask;

    public PriceServer(int port) {
        PriceStreamingApp app = new PriceStreamingApp(quoteQueue);
        server = new FixServer(port, app,
                new AcceptAllAuthenticator(),
                new InMemorySessionRepository()
        );
    }

    public static void main(String[] args) throws InterruptedException {
        PriceServer priceServer = new PriceServer(DEFAULT_PORT);
        priceServer.start();
    }

    public void start() throws InterruptedException {
        generatorTask = new QuoteGeneratorTask(quoteQueue);
        generator = new Thread(generatorTask, "QuoteGenerator");
        server.start();
        generator.start();
    }

    public void stop() throws InterruptedException {
        generatorTask.stop();
        server.stop();
        generator.join();
    }
}
