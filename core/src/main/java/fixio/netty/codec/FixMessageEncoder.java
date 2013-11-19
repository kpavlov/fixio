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

import fixio.fixprotocol.Field;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageFragment;
import fixio.fixprotocol.FixMessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FixMessageEncoder extends MessageToByteEncoder<FixMessage> {

    final static Charset CHARSET = StandardCharsets.US_ASCII;

    public FixMessageEncoder() {
        super(FixMessage.class);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, FixMessage msg, ByteBuf out) throws Exception {
        final int initialOffset = out.writerIndex();
        final FixMessageHeader header = msg.getHeader();

        final ByteBuf bodyBuf = createBodyBuf(msg, header);

        int bodyLength = bodyBuf.writerIndex();
        final ByteBuf headBuf = Unpooled.buffer();
        // begin string
        writeField(8, header.getBeginString(), headBuf);
        // body length
        writeField(9, String.valueOf(bodyLength), headBuf);

        out.writeBytes(headBuf);
        out.writeBytes(bodyBuf);

        int checksum = calculateChecksum(out, initialOffset);

        // Checksum
        writeField(10, String.format("%1$03d", checksum), out);
        ctx.flush();
    }

    private ByteBuf createBodyBuf(FixMessage msg, FixMessageHeader header) {
        final ByteBuf payloadBuf = Unpooled.buffer();

        encodeHeader(header, payloadBuf);

        // message body
        for (FixMessageFragment component : msg.getBody()) {
            if (component instanceof Field) {
                writeField(component.getTagNum(), ((Field) component).getValue(), payloadBuf);
            }
        }
        return payloadBuf;
    }

    private void encodeHeader(FixMessageHeader header, ByteBuf out) {

        // message type
        writeField(35, header.getMessageType(), out);

        // SenderCompID
        writeField(49, header.getSenderCompID(), out);

        // TargetCompID
        writeField(56, header.getTargetCompID(), out);

        // MsgSeqNum
        writeField(34, String.valueOf(header.getMsgSeqNum()), out);
    }

    private void writeField(int fieldNum, String value, ByteBuf out) {
        out.writeBytes((String.valueOf(fieldNum) + "=").getBytes(CHARSET));
        out.writeBytes(value.getBytes(CHARSET));
        out.writeByte(1);
    }

    static int calculateChecksum(ByteBuf buf, int offset) {
        int sum = 0;
        for (int i = offset; i < buf.writerIndex(); i++) {
            sum += buf.getByte(i);
        }
        return sum % 256;
    }
}
