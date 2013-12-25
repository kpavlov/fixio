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

package fixio.netty.codec;

import fixio.fixprotocol.SimpleFixMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FixMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    private SimpleFixMessage message;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        assert (in != null) : "No Buffer";
        assert (in.readableBytes() > 3) : "Buffer too small";

        int tag = readTagNum(in);
        if (tag == -1) {
            return;
        }
        String value = in.toString(StandardCharsets.US_ASCII);
        in.skipBytes(in.readableBytes());
        switch (tag) {
            case 8:
                if (message != null) {
                    throw new DecoderException("Unexpected BeginString tag.");
                }
                message = new SimpleFixMessage();
                message.add(tag, value);
                break;
            case 10:
                appendField(tag, value);
                out.add(message);
                message = null;
                break;
            default:
                appendField(tag, value);
        }
    }

    private void appendField(int tag, String value) {
        if (message == null) {
            throw new DecoderException("BeginString tag expected, but got: " + tag + "=" + value);
        }
        message.add(tag, value);
    }

    private int readTagNum(ByteBuf in) {
        int result = 0;

        while (in.isReadable()) {
            byte b = in.readByte();
            if (b == '=') {
                return result;
            }
            result = result * 10 + (b - '0');
        }
        return -1;
    }
}
