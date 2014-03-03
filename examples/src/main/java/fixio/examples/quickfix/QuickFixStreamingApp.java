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

import fixio.examples.generator.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.MktBidPx;
import quickfix.field.MktOfferPx;
import quickfix.field.MsgType;
import quickfix.field.QuoteReqID;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class QuickFixStreamingApp implements Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuickFixStreamingApp.class);
    private final BlockingQueue<Quote> quoteQueue;
    private final Map<String, SessionID> subscriptions = new ConcurrentHashMap<>();

    public QuickFixStreamingApp(BlockingQueue<Quote> quoteQueue) {
        this.quoteQueue = quoteQueue;

        StreamingWorker streamingWorker = new StreamingWorker();
        new Thread(streamingWorker, "StreamingWorker").start();
    }

    private static Message createQuoteMessage(String reqId, Quote quote) {
        quickfix.fix44.Quote message = new quickfix.fix44.Quote();
        message.setString(QuoteReqID.FIELD, reqId);

        message.setDouble(MktBidPx.FIELD, quote.getBid(), 2);
        message.setDouble(MktOfferPx.FIELD, quote.getBid(), 2);

        return message;
    }

    @Override
    public void onCreate(SessionID sessionID) {
    }

    @Override
    public void onLogon(SessionID sessionID) {
    }

    @Override
    public void onLogout(SessionID sessionID) {
        stopStreaming(sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {

    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        try {
            String msgType = message.getHeader().getString(35);
            switch (msgType) {
                case MsgType.QUOTE_REQUEST:
                    String reqId = message.getString(QuoteReqID.FIELD);
                    subscriptions.put(reqId, sessionID);

                    LOGGER.debug("Subscribed with QuoteReqID={}", reqId);
                    break;
                case MsgType.QUOTE_CANCEL:
                    reqId = message.getString(QuoteReqID.FIELD);
                    subscriptions.remove(reqId);
                    LOGGER.debug("Unsubscribed with QuoteReqID={}", reqId);
                    break;

            }
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        }
    }

    private void stopStreaming(SessionID sessionID) {
        ArrayList<String> requestsToCancel = new ArrayList<>(subscriptions.size());
        for (Map.Entry<String, SessionID> entry : subscriptions.entrySet()) {
            if (entry.getValue() == sessionID) {
                requestsToCancel.add(entry.getKey());
            }
        }
        for (String reqId : requestsToCancel) {
            subscriptions.remove(reqId);
        }
        LOGGER.info("Streaming Stopped for {}", sessionID);
    }

    private class StreamingWorker implements Runnable {

        private volatile boolean stopping;

        @Override
        public void run() {
            Quote quote = null;
            while (!stopping) {
                try {
                    quote = quoteQueue.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("Interrupted queue", e);
                }
                for (Map.Entry<String, SessionID> subscriptionEntry : subscriptions.entrySet()) {
                    Message quoteMessage = createQuoteMessage(subscriptionEntry.getKey(), quote);
                    try {
                        Session.sendToTarget(quoteMessage, subscriptionEntry.getValue());
                    } catch (SessionNotFound e) {
                        LOGGER.error("Can't send message", e);
                    }
                }
            }
        }

        public void stopWorker() {
            stopping = true;
        }
    }
}
