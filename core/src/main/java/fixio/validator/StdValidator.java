package fixio.validator;

import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import fixio.fixprotocol.FixMessageBuilderImpl;
import io.netty.channel.ChannelHandlerContext;

public class StdValidator  implements IFixMsgValidator {

	public StdValidator(){

	}
	@Override
	public boolean isValidate(ChannelHandlerContext ctx, FixMessage msg) {
		return true;
	}

	@Override
	public boolean validate(ChannelHandlerContext ctx, FixMessage msg) {
		return true;
    }

    @Override
    public FixMessageBuilder getErrorMsg() {
        return new FixMessageBuilderImpl();
    }
}
