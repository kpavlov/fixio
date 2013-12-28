/*
 * Copyright 2013 The FIX.io Project
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
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

class PriceStreamingApp extends FixApplicationAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceStreamingApp.class);
    private final ThreadLocal<ScheduledFuture> streamingFutureRef = new ThreadLocal<>();

    private static FixMessage createQuote(String reqId) {
        SimpleFixMessage quote = new SimpleFixMessage(MessageTypes.QUOTE);
        quote.add(FieldType.QuoteReqID, reqId);

        return quote;
    }

    @Override
    protected void onLogon(ChannelHandlerContext ctx, LogonEvent msg) {
        LOGGER.info("Client Connected.");
    }

    @Override
    protected void onLogout(ChannelHandlerContext ctx, LogoutEvent msg) {
        LOGGER.info("Logout.");
        stopStreaming();
    }

    private void stopStreaming() {
        ScheduledFuture scheduledFuture = streamingFutureRef.get();
        if (scheduledFuture == null) {
            return;
        }
        scheduledFuture.cancel(true);
        streamingFutureRef.remove();
        LOGGER.info("Streaming Stopped.");
    }

    @Override
    protected void onMessage(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        switch (msg.getMessageType()) {
            case MessageTypes.QUOTE_REQUEST:
                startStreaming(ctx, msg);
                break;
            case MessageTypes.QUOTE_CANCEL:
                stopStreaming();
                break;
        }
    }

    private void startStreaming(final ChannelHandlerContext ctx, FixMessage msg) {
        final String reqId = msg.getString(FieldType.QuoteReqID);

        ScheduledFuture<?> streamingFuture = ctx.executor().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                FixMessage quote = createQuote(reqId);
                LOGGER.trace("Submit quote.");
                ctx.writeAndFlush(quote);
            }
        }, 5, 10, TimeUnit.NANOSECONDS);

        streamingFutureRef.set(streamingFuture);
        LOGGER.info("Streaming Started.");
    }

}
