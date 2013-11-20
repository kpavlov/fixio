package fixio.handlers;


import fixio.events.LogonEvent;
import fixio.events.LogoutEvent;
import fixio.fixprotocol.FixMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

@ChannelHandler.Sharable
public class FixApplicationAdapter extends MessageToMessageDecoder<Object> implements AdminEventHandler, FixMessageHandler {

    @Override
    protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (msg instanceof FixMessage) {
            onMessage(ctx, (FixMessage) msg, out);
        } else if (msg instanceof LogonEvent) {
            onLogon(ctx, (LogonEvent) msg);
        } else if (msg instanceof LogoutEvent) {
            onLogout(ctx, (LogoutEvent) msg);
        }
    }

    protected void onLogon(ChannelHandlerContext ctx, LogonEvent msg) {

    }

    protected void onLogout(ChannelHandlerContext ctx, LogoutEvent msg) {

    }

    protected void onMessage(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("cause = " + cause);
        cause.printStackTrace();
        ctx.close().sync();
//        super.exceptionCaught(ctx, cause);
    }
}
