package fixio.handlers;

import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import io.netty.channel.ChannelHandlerContext;

/**
 * User: Harvey
 * Date: 2014/2/19
 * Time: 下午 4:10
 */
public interface IFixMsgHandler {
	public boolean isProcess(ChannelHandlerContext ctx, FixMessage msg);
    public boolean isProcess(ChannelHandlerContext ctx, FixMessageBuilder msg);
	public void process(ChannelHandlerContext ctx, FixMessage msg);
    public void beforeSendMessage(ChannelHandlerContext ctx, FixMessageBuilder messageBuilder);
}
