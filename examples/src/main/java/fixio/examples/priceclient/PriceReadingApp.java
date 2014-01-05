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

import fixio.events.LogonEvent;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.Group;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.SimpleFixMessage;
import fixio.handlers.FixApplicationAdapter;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class PriceReadingApp extends FixApplicationAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceReadingApp.class);
    public static final int MAX_QUOTE_COUNT = 100_000;
    private int counter;
    private long startTimeNanos;
    private boolean finished;
    private String quoteRequestId;

    @Override
    protected void onLogon(ChannelHandlerContext ctx, LogonEvent msg) {
        counter = 0;
        ctx.writeAndFlush(createQuoteRequest());
        startTimeNanos = System.nanoTime();
    }

    @Override
    protected void onMessage(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        assert (msg != null) : "Message can't be null";
        switch (msg.getMessageType()) {
            case MessageTypes.QUOTE:
                onQuote(msg);
                break;
        }
        if (counter % 10000 == 0) {
            LOGGER.debug("Read {} Quotes", counter);
        }
        if (counter > MAX_QUOTE_COUNT && !finished) {
            finished = true;

            long timeMillis = (System.nanoTime() - startTimeNanos) / 1000000;
            LOGGER.info("Read {} Quotes in {} ms, ~{} Quote/sec", counter, timeMillis, counter * 1000.0 / timeMillis);

            ctx.writeAndFlush(createQuoteCancel()).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void onQuote(FixMessage quote) {
        LOGGER.trace("quote = {}", quote);
        counter++;
    }

    private FixMessage createQuoteCancel() {
        SimpleFixMessage quoteCancel = new SimpleFixMessage(MessageTypes.QUOTE_CANCEL);//QuoteCancel
        quoteCancel.add(298, 4); //QuoteRequestType=AUTOMATIC
        quoteCancel.add(131, quoteRequestId); //quoteReqId
        return quoteCancel;
    }

    private FixMessage createQuoteRequest() {
        SimpleFixMessage quoteRequest = new SimpleFixMessage(MessageTypes.QUOTE_REQUEST);
        quoteRequestId = Long.toHexString(System.currentTimeMillis());
        quoteRequest.add(131, quoteRequestId); //quoteReqId
        String clientReqId = quoteRequestId + counter;
        quoteRequest.add(11, clientReqId);


        Group instrument1 = quoteRequest.newGroup(146);//noRelatedSym
        instrument1.add(55, "EUR/USD");
        instrument1.add(167, "CURRENCY");

        Group instrument2 = quoteRequest.newGroup(146);//noRelatedSym
        instrument2.add(55, "EUR/CHF");
        instrument2.add(167, "CURRENCY");

        quoteRequest.add(303, 2); //QuoteRequestType=AUTOMATIC
        return quoteRequest;
    }
}
