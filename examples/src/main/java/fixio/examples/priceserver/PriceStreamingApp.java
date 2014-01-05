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

import fixio.events.LogonEvent;
import fixio.events.LogoutEvent;
import fixio.fixprotocol.FieldType;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.SimpleFixMessage;
import fixio.handlers.FixApplicationAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;

class PriceStreamingApp extends FixApplicationAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceStreamingApp.class);
    private final BlockingDeque<Quote> quoteQueue;
    private final Map<String, ChannelHandlerContext> subscriptions = new ConcurrentHashMap<>();

    public PriceStreamingApp(BlockingDeque<Quote> quoteQueue) {
        this.quoteQueue = quoteQueue;
        StreamingWorker streamingWorker = new StreamingWorker();
        new Thread(streamingWorker, "StreamingWorker").start();
    }

    ;

    private static FixMessage createQuoteMessage(String reqId, Quote quote) {
        SimpleFixMessage message = new SimpleFixMessage(MessageTypes.QUOTE);
        message.add(FieldType.QuoteReqID, reqId);

        message.add(645, String.format("%1$.5f", quote.getBid()));// MktBidPx
        message.add(646, String.format("%1$.5f", quote.getOffer()));//MktOfferPx

        return message;
    }

    private static void publish(final ChannelHandlerContext ctx, String reqId, Quote quote) {
        FixMessage message = createQuoteMessage(reqId, quote);
        LOGGER.trace("Submit quote.");
        ctx.writeAndFlush(message);
    }

    @Override
    protected void onLogon(ChannelHandlerContext ctx, LogonEvent msg) {
        LOGGER.info("Client Connected.");
    }

    @Override
    protected void onLogout(ChannelHandlerContext ctx, LogoutEvent msg) {
        LOGGER.info("Logout.");
        stopStreaming(ctx);
    }

    private void stopStreaming(ChannelHandlerContext ctx) {
        ArrayList<String> requestsToCancel = new ArrayList<>();
        for (Map.Entry<String, ChannelHandlerContext> entry : subscriptions.entrySet()) {
            if (entry.getValue() == ctx) {
                requestsToCancel.add(entry.getKey());
            }
        }
        for (String reqId : requestsToCancel) {
            subscriptions.remove(reqId);
        }
        LOGGER.info("Streaming Stopped for {}", ctx);
    }

    @Override
    protected void onMessage(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        String reqId;
        switch (msg.getMessageType()) {
            case MessageTypes.QUOTE_REQUEST:
                reqId = msg.getString(FieldType.QuoteReqID);
                subscriptions.put(reqId, ctx);
                LOGGER.debug("Subscribed with QuoteReqID={}", reqId);
                break;
            case MessageTypes.QUOTE_CANCEL:
                reqId = msg.getString(FieldType.QuoteReqID);
                subscriptions.remove(reqId);
                LOGGER.debug("Unsubscribed with QuoteReqID={}", reqId);
                break;
        }
    }

    private class StreamingWorker implements Runnable {

        private volatile boolean stopping;

        @Override
        public void run() {
            Quote quote = null;
            while (!stopping) {
                try {
                    quote = quoteQueue.takeFirst();
                } catch (InterruptedException e) {

                }
                for (Map.Entry<String, ChannelHandlerContext> subscriptionEntry : subscriptions.entrySet()) {
                    publish(subscriptionEntry.getValue(), subscriptionEntry.getKey(), quote);
                }
            }
        }

        public void stopWorker() {
            stopping = true;
        }
    }
}
