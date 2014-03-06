package fixio.validator;

import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import io.netty.channel.ChannelHandlerContext;

public interface IFixMsgValidator {
	public boolean isValidate(ChannelHandlerContext ctx, FixMessage msg);
	public boolean validate(ChannelHandlerContext ctx, FixMessage msg);
	public FixMessageBuilder getErrorMsg();
}
