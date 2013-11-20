package fixio.examples.priceclient;

import fixio.events.LogonEvent;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.SimpleFixMessage;
import fixio.handlers.FixApplicationAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class PriceReadingApp extends FixApplicationAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceReadingApp.class);
    private int counter;

    @Override
    protected void onLogon(ChannelHandlerContext ctx, LogonEvent msg) {
        counter = 0;
        ctx.writeAndFlush(createQuoteRequest());
    }

    private FixMessage createQuoteRequest() {
        SimpleFixMessage quoteRequest = new SimpleFixMessage(MessageTypes.QUOTE_REQUEST);
        quoteRequest.add(303, 2); //QuoteRequestType=AUTOMATIC
        return quoteRequest;
    }

    private FixMessage createQuoteCancel() {
        SimpleFixMessage quoteCancel = new SimpleFixMessage(MessageTypes.QUOTE_CANCEL);//QuoteCancel
        quoteCancel.add(298, 4); //QuoteRequestType=AUTOMATIC
        return quoteCancel;
    }

    @Override
    protected void onMessage(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        switch (msg.getMessageType()) {
            case MessageTypes.QUOTE:
                onQuote(msg);
                break;
        }
        if (counter > 10000) {
            ctx.writeAndFlush(createQuoteCancel());
        }
    }

    private void onQuote(FixMessage quote) {
        LOGGER.debug("quote = {}", quote);
        counter++;

    }
}
