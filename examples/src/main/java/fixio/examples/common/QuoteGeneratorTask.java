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
package fixio.examples.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

public class QuoteGeneratorTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteGeneratorTask.class);
    private final BlockingQueue<Quote> receiver;

    private static int DATA_SIZE = 1000;
    private final Quote[] data = new Quote[DATA_SIZE];

    public QuoteGeneratorTask(BlockingQueue<Quote> receiverQueue) {
        receiver = receiverQueue;
        for (int i = 0; i < DATA_SIZE; i++) {
            data[i] = createQuote(i);
        }
    }

    @Override
    public void run() {
        LOGGER.info("Started");
        final Thread thread = Thread.currentThread();
        int t = 0;
        while (!thread.isInterrupted()) {
            if (receiver.remainingCapacity() > 1000) {
                try {
                    Quote quote = data[t % DATA_SIZE];
                    t++;
                    receiver.put(quote);
                    Thread.yield();
                } catch (InterruptedException e) {
                    LOGGER.info("Interrupted.");
                    thread.interrupt();
                    return;
                } catch (Throwable e) {
                    LOGGER.error("Unable to submit quote.", e);
                }
            }
            Thread.yield();
        }
        LOGGER.info("Stopped");
    }

    private static Quote createQuote(double t) {
        double bid = Math.sin(t / 100) * 2 + 0.5;
        double offer = Math.sin(t - 10 / 100) * 2 + 0.5;
        return new Quote(bid, offer);
    }

    public void stop() {
        Thread.currentThread().interrupt();
    }
}
