/*
 * Copyright 2014 The FIX.io Project
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

import fixio.fixprotocol.FixConst;
import fixio.fixprotocol.FixMessageBuilder;
import fixio.fixprotocol.FixMessageFragment;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.Group;
import fixio.fixprotocol.GroupField;
import fixio.fixprotocol.fields.AbstractField;
import fixio.fixprotocol.fields.DateTimeFormatterWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ChannelHandler.Sharable
public class FixMessageEncoder extends MessageToByteEncoder<FixMessageBuilder> {

    private static final Charset CHARSET = StandardCharsets.US_ASCII;

    private static void validateRequiredFields(FixMessageHeader header) {
        if (header.getBeginString() == null) {
            throw new IllegalArgumentException("BeginString is required.");
        }
        if (header.getMessageType() == null) {
            throw new IllegalArgumentException("MessageType is required.");
        }
        if (header.getSenderCompID() == null) {
            throw new IllegalArgumentException("SenderCompID is required.");
        }
        if (header.getTargetCompID() == null) {
            throw new IllegalArgumentException("TargetCompID is required.");
        }
    }

    private static void writeField(int fieldNum, AbstractField field, ByteBuf out) {
        out.writeBytes(Integer.toString(fieldNum).getBytes(CHARSET));
        out.writeByte('=');
        out.writeBytes(field.getBytes());
        out.writeByte(1);
    }

    private static void writeField(int fieldNum, String stringValue, ByteBuf out) {
        out.writeBytes(Integer.toString(fieldNum).getBytes(CHARSET));
        out.writeByte('=');
        out.writeBytes(stringValue.getBytes(CHARSET));
        out.writeByte(1);
    }

    static int calculateChecksum(ByteBuf buf, int offset) {
        int sum = 0;
        for (int i = offset; i < buf.writerIndex(); i++) {
            sum += buf.getByte(i);
        }
        return sum % 256;
    }

    private static void encodeHeader(FixMessageHeader header, ByteBuf out) {

        // message type
        writeField(35, header.getMessageType(), out);

        // SenderCompID
        writeField(49, header.getSenderCompID(), out);

        // TargetCompID
        writeField(56, header.getTargetCompID(), out);

        // MsgSeqNum
        writeField(34, Integer.toString(header.getMsgSeqNum()), out);

        // SenderSubID
        if (header.getSenderSubID() != null && !"".equals(header.getSenderSubID())) {
            writeField(50, header.getSenderSubID(), out);
        }
        // SenderLocationID
        if (header.getSenderLocationID() != null && !"".equals(header.getSenderLocationID())) {
            writeField(142, header.getSenderLocationID(), out);
        }

        // TargetSubID
        if (header.getTargetSubID() != null && !"".equals(header.getTargetSubID())) {
            writeField(57, header.getTargetSubID(), out);
        }
        // TargetLocationID
        if (header.getTargetLocationID() != null && !"".equals(header.getTargetLocationID())) {
            writeField(143, header.getTargetLocationID(), out);
        }

        // SendingTime
        DateTimeFormatterWrapper formatter = (header.getDateTimeFormatter()!=null)? header.getDateTimeFormatter(): FixConst.DATE_TIME_FORMATTER_MILLIS;
        String timeStr = formatter.format(header.getSendingTime());
        writeField(52, timeStr, out);

        // customize tag
        List<FixMessageFragment> customFields = header.getCustomFields();
        if (customFields != null) {
            for (FixMessageFragment customField : customFields) {
                encodeMessageFragment(out, customField);
            }
        }
    }

    private static int fillBodyBuf(final ByteBuf payloadBuf,
                                   FixMessageBuilder msg,
                                   FixMessageHeader header) {

        encodeHeader(header, payloadBuf);

        // message body
        for (FixMessageFragment component : msg.getBody()) {
            encodeMessageFragment(payloadBuf, component);
        }
        return payloadBuf.writerIndex();
    }

    private static void encodeMessageFragment(ByteBuf payloadBuf,
                                              FixMessageFragment messageFragment) {
        if (messageFragment instanceof AbstractField) {
            writeField(messageFragment.getTagNum(), (AbstractField) messageFragment, payloadBuf);
        } else if (messageFragment instanceof GroupField) {
            GroupField groupField = (GroupField) messageFragment;
            writeField(groupField.getTagNum(), Integer.toString(groupField.getGroupCount()), payloadBuf);
            for (Group c : groupField.getValue()) {
                List<FixMessageFragment> contents = c.getContents();
                for (FixMessageFragment content : contents) {
                    encodeMessageFragment(payloadBuf, content);
                }
            }
        }
    }

    static void writeChecksumField(ByteBuf out, int value) {
        int x2 = value / 100;
        int x1 = (value - x2 * 100) / 10;
        int x0 = value - x2 * 100 - x1 * 10;
        out.ensureWritable(7);
        out.writeByte('1');
        out.writeByte('0');
        out.writeByte('=');
        out.writeByte('0' + x2);
        out.writeByte('0' + x1);
        out.writeByte('0' + x0);
        out.writeByte((byte) 1);
    }

    @Override
    public void encode(ChannelHandlerContext ctx,
                       FixMessageBuilder msg,
                       ByteBuf out) throws Exception {
        final FixMessageHeader header = msg.getHeader();
        validateRequiredFields(header);

        final int initialOffset = out.writerIndex();

        final ByteBufAllocator byteBufAllocator = ctx.alloc();

        final ByteBuf bodyBuf = byteBufAllocator.buffer();
        final ByteBuf headBuf = byteBufAllocator.buffer();

        int bodyLength = fillBodyBuf(bodyBuf, msg, header);

        // begin string
        writeField(8, header.getBeginString(), headBuf);
        // body length
        writeField(9, Integer.toString(bodyLength), headBuf);

        out.writeBytes(headBuf);
        out.writeBytes(bodyBuf);

        int checksum = calculateChecksum(out, initialOffset);

        // Checksum
        writeChecksumField(out, checksum);
        ctx.flush();

        headBuf.release();
        bodyBuf.release();
        assert (headBuf.refCnt() == 0);
        assert (bodyBuf.refCnt() == 0);
    }
}
