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

import fixio.fixprotocol.*;
import fixio.fixprotocol.fields.AbstractField;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@ChannelHandler.Sharable
public class FixMessageEncoder extends MessageToByteEncoder<FixMessageBuilder> {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final Charset CHARSET = StandardCharsets.US_ASCII;
    private static final String UTC_TIMESTAMP_WITH_MILLIS_PATTERN = "yyyyMMdd-HH:mm:ss.SSS";
    private static final ThreadLocal<DateFormat> sdf = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            SimpleDateFormat sdf = new SimpleDateFormat(UTC_TIMESTAMP_WITH_MILLIS_PATTERN);
            sdf.setTimeZone(UTC);
            sdf.setDateFormatSymbols(new DateFormatSymbols(Locale.US));
            return sdf;
        }
    };

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
        out.writeBytes(String.valueOf(fieldNum).getBytes(CHARSET));
        out.writeByte('=');
        out.writeBytes(field.getBytes());
        out.writeByte(1);
    }

    private static void writeField(int fieldNum, String stringValue, ByteBuf out) {
        out.writeBytes(String.valueOf(fieldNum).getBytes(CHARSET));
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

        // SenderCompSubID
        if(header.getSenderSubID()!=null && !"".equals(header.getSenderSubID())){
            writeField(50, header.getSenderSubID(), out);
        }

        // TargetCompID
        writeField(56, header.getTargetCompID(), out);

        // TargetCompSubID
        if(header.getTargetSubID()!=null && !"".equals(header.getTargetSubID())){
            writeField(57, header.getTargetSubID(), out);
        }

        // MsgSeqNum
        writeField(34, String.valueOf(header.getMsgSeqNum()), out);

        // SendingTime
        String timeStr = sdf.get().format(new Date(header.getSendingTime()));
        writeField(52, timeStr, out);

        // customize tag
        for (FixMessageFragment component : header.getCustField()) {
            encodeComponent(out, component);
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
            writeField(groupField.getTagNum(), String.valueOf(groupField.getGroupCount()), payloadBuf);
            for (Group c : groupField.getGroups()) {
                List<FixMessageFragment> contents = c.getContents();
                for (int j = 0; j < contents.size(); j++) {
                    encodeMessageFragment(payloadBuf, contents.get(j));
                }
            }
        }
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
        writeField(9, String.valueOf(bodyLength), headBuf);

        out.writeBytes(headBuf);
        out.writeBytes(bodyBuf);

        int checksum = calculateChecksum(out, initialOffset);

        // Checksum
        writeField(10, String.format("%1$03d", checksum), out);
        ctx.flush();

        headBuf.release();
        bodyBuf.release();
        assert (headBuf.refCnt() == 0);
        assert (bodyBuf.refCnt() == 0);
    }
}
