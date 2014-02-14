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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

class QuoteGeneratorTask implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(QuoteGeneratorTask.class);
    private final BlockingQueue<Quote> receiver;
    private int t;
    private volatile boolean isStopping = false;

    QuoteGeneratorTask(BlockingQueue<Quote> receiverQueue) {
        receiver = receiverQueue;
    }

    @Override
    public void run() {
        LOGGER.info("Started");
        while (!isStopping) {
            try {
                double bid = Math.sin((double) t / 100) * 2 + 0.5;
                double offer = Math.sin((double) t - 10 / 100) * 2 + 0.5;
                Quote quote = new Quote(bid, offer);
                t++;
                if (!receiver.offer(quote)) {
                    // slow quote reader. We may discard the oldest quite in the queue.
                    for (int i = 0; i < 10; i++) {
                        receiver.poll();
                    }
                }

            } catch (Throwable e) {
                LOGGER.error("Unable to sumbit quote.", e);
            }
            Thread.yield();
        }
        LOGGER.info("Stopped");
    }

    public void stop() {
        isStopping = true;
    }
}
