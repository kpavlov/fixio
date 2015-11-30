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
package fixio.examples.quickfix;

import fixio.examples.common.Quote;
import fixio.examples.common.QuoteGeneratorTask;
import quickfix.*;

import java.util.concurrent.ArrayBlockingQueue;

public class QuickFixServer {

    private final ArrayBlockingQueue<Quote> quoteQueue = new ArrayBlockingQueue<>(8192);
    private Thread generator;
    private QuoteGeneratorTask generatorTask;
    private Acceptor acceptor;

    public QuickFixServer() throws ConfigError {
        SessionSettings settings = new SessionSettings(getClass().getResourceAsStream("/quickfix/quickfix-server.properties"));
        MessageStoreFactory storeFactory = new MemoryStoreFactory();
        LogFactory logFactory = new SLF4JLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();

        Application application = new QuickFixStreamingApp(quoteQueue);

        acceptor = new SocketAcceptor(application, storeFactory, settings, logFactory, messageFactory);
    }

    public void start() throws ConfigError {
        generatorTask = new QuoteGeneratorTask(quoteQueue);
        generator = new Thread(generatorTask, "QuoteGenerator");
        acceptor.start();
        generator.start();
    }

    public void stop() throws InterruptedException {
        generatorTask.stop();
        acceptor.stop();
        generator.join();
    }

    public static void main(String[] args) throws ConfigError {
        QuickFixServer quickFixServer = new QuickFixServer();
        quickFixServer.start();
    }
}
