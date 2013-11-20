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

    private static FixMessage createQuote() {
        SimpleFixMessage quoteRequest = new SimpleFixMessage(MessageTypes.QUOTE);

        return quoteRequest;
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
        streamingFutureRef.get().cancel(true);
        streamingFutureRef.remove();
        LOGGER.info("Stoped Streaming.");
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
        String reqId = msg.getString(FieldType.QuoteReqID);

        ScheduledFuture<?> streamingFuture = ctx.executor().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                FixMessage quote = createQuote();
                LOGGER.debug("Submit quote.");
                ctx.writeAndFlush(quote);
            }
        }, 5, 1, TimeUnit.MILLISECONDS);

        streamingFutureRef.set(streamingFuture);
        LOGGER.info("Started Streaming.");
    }

}
