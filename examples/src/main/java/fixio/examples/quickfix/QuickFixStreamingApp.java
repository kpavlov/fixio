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

import fixio.examples.common.AbstractQouteStreamingWorker;
import fixio.examples.common.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.MktBidPx;
import quickfix.field.MktOfferPx;
import quickfix.field.MsgType;
import quickfix.field.QuoteReqID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class QuickFixStreamingApp implements Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuickFixStreamingApp.class);
    private static final MsgType QUOTE_MSG_TYPE = new MsgType(MsgType.QUOTE);
    private final Map<String, SessionID> subscriptions = new ConcurrentHashMap<>();

    public QuickFixStreamingApp(BlockingQueue<Quote> quoteQueue) {
        StreamingWorker streamingWorker = new StreamingWorker(quoteQueue);
        new Thread(streamingWorker, "StreamingWorker").start();
    }

    private static Message createQuoteMessage(String reqId, Quote quote) throws InvalidMessage {
        Message message = new Message();
        message.getHeader().setField(QUOTE_MSG_TYPE);
        message.setField(new QuoteReqID(reqId));

        message.setField(new DoubleField(MktBidPx.FIELD, quote.getBid(), 2));
        message.setField(new DoubleField(MktOfferPx.FIELD, quote.getBid(), 2));

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
        requestsToCancel.addAll(subscriptions.entrySet().stream().filter(entry -> entry.getValue() == sessionID).map(Map.Entry::getKey).collect(Collectors.toList()));
        requestsToCancel.forEach(subscriptions::remove);
        LOGGER.info("Streaming Stopped for {}", sessionID);
    }

    private class StreamingWorker extends AbstractQouteStreamingWorker {

        public StreamingWorker(BlockingQueue<Quote> quoteQueue) {
            super(quoteQueue);
        }

        @Override
        protected void sendQuotes(List<Quote> buffer) {
            buffer.forEach(quote -> {
                for (Map.Entry<String, SessionID> subscriptionEntry : subscriptions.entrySet()) {
                    try {
                        Message quoteMessage = createQuoteMessage(subscriptionEntry.getKey(), quote);
                        Session.sendToTarget(quoteMessage, subscriptionEntry.getValue());
                    } catch (SessionNotFound e) {
                        LOGGER.error("Can't send message", e);
                    } catch (InvalidMessage e) {
                        LOGGER.error("Can't send invalid message", e);
                    }
                }
            });
        }
    }
}
