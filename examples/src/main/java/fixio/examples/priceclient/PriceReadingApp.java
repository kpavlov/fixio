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
    private int counter;

    @Override
    protected void onLogon(ChannelHandlerContext ctx, LogonEvent msg) {
        counter = 0;
        ctx.writeAndFlush(createQuoteRequest());
    }

    private FixMessage createQuoteRequest() {
        SimpleFixMessage quoteRequest = new SimpleFixMessage(MessageTypes.QUOTE_REQUEST);
        String quoteRequestId = Long.toHexString(System.currentTimeMillis());
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

    private FixMessage createQuoteCancel() {
        SimpleFixMessage quoteCancel = new SimpleFixMessage(MessageTypes.QUOTE_CANCEL);//QuoteCancel
        quoteCancel.add(298, 4); //QuoteRequestType=AUTOMATIC
        return quoteCancel;
    }

    @Override
    protected void onMessage(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
        assert (msg != null) : "Message can't be null";
        switch (msg.getMessageType()) {
            case MessageTypes.QUOTE:
                onQuote(msg);
                break;
        }
        if (counter > 100_000) {
            ctx.writeAndFlush(createQuoteCancel()).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void onQuote(FixMessage quote) {
        LOGGER.trace("quote = {}", quote);
        counter++;

    }
}
