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

import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import fixio.fixprotocol.FixMessageBuilderImpl;
import fixio.fixprotocol.FixMessageHeader;
import fixio.fixprotocol.Group;
import fixio.fixprotocol.MessageTypes;
import fixio.fixprotocol.fields.FieldFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FixMessageEncoderTest {

    private static FixMessageEncoder encoder;
    private final ZonedDateTime timestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(123456789), ZoneId.of("UTC"));
    @Mock
    private ChannelHandlerContext ctx;
    @Mock
    private ByteBufAllocator byteBufAllocator;
    private FixMessageBuilder messageBuilder;
    private ByteBuf out;

    @BeforeAll
    static void beforeClass() {
        encoder = new FixMessageEncoder();
    }

    @BeforeEach
    void setUp() {
        when(ctx.alloc()).thenReturn(byteBufAllocator);
        when(byteBufAllocator.buffer()).thenReturn(Unpooled.buffer(), Unpooled.buffer());

        FixMessageBuilder fixMessage = new FixMessageBuilderImpl();

        FixMessageHeader header = fixMessage.getHeader();

        header.setBeginString(FixMessage.FIX_4_2);
        header.setMessageType(MessageTypes.HEARTBEAT);
        header.setSenderCompID("SenderCompID");
        header.setTargetCompID("TargetCompID");
        header.setMsgSeqNum(2);

        fixMessage.add(1001, "test2");
        fixMessage.add(1000, "test1");

        this.messageBuilder = fixMessage;
        fixMessage.getHeader().setSendingTime(timestamp);

        out = Unpooled.buffer();
    }

    @Test
    void failIfNoBeginStringCompID() {
        messageBuilder.getHeader().setBeginString(null);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->

                encoder.encode(ctx, messageBuilder, out));
    }

    @Test
    void failIfNoMsgType() {
        messageBuilder.getHeader().setMessageType(null);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->

                encoder.encode(ctx, messageBuilder, out));
    }

    @Test
    void failIfNoSenderCompID() {
        messageBuilder.getHeader().setSenderCompID(null);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->

                encoder.encode(ctx, messageBuilder, out));
    }

    @Test
    void failIfNoTargetCompID() {
        messageBuilder.getHeader().setTargetCompID(null);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->

                encoder.encode(ctx, messageBuilder, out));
    }

    @Test
    void encode() throws Exception {

        encoder.encode(ctx, messageBuilder, out);

        verify(ctx).flush();

        String expectedString = "8=FIX.4.2\u00019=89\u000135=0\u000149=SenderCompID\u000156=TargetCompID\u000134=2\u000152=19700102-10:17:36.789\u00011001=test2\u00011000=test1\u000110=204\u0001";

        assertResult(expectedString);
    }

    @Test
    void encodeWithCustomHeader() throws Exception {
        messageBuilder.getHeader().setCustomFields(Arrays.asList(
                FieldFactory.fromIntValue(1128, 9),
                FieldFactory.fromStringValue(1129, "1.0")
        ));

        encoder.encode(ctx, messageBuilder, out);

        verify(ctx).flush();

        String expectedString = "8=FIX.4.2\u00019=105\u000135=0\u000149=SenderCompID\u000156=TargetCompID\u000134=2\u000152=19700102-10:17:36.789\u00011128=9\u00011129=1.0\u00011001=test2\u00011000=test1\u000110=206\u0001";

        assertResult(expectedString);
    }

    @Test
    void encodeWithGroup() throws Exception {

        Group group1 = messageBuilder.newGroup(1002, 2);
        group1.add(1003, "g1-1");
        group1.add(1004, "g1-2");

        Group group2 = messageBuilder.newGroup(1002);
        group2.add(1003, "g2-1");
        group2.add(1004, "g2-2");

        encoder.encode(ctx, messageBuilder, out);

        verify(ctx).flush();

        String expectedString = "8=FIX.4.2\u00019=136\u000135=0\u000149=SenderCompID\u000156=TargetCompID\u000134=2\u000152=19700102-10:17:36.789\u00011001=test2\u00011000=test1\u00011002=2\u00011003=g1-1\u00011004=g1-2\u00011003=g2-1\u00011004=g2-2\u000110=014\u0001";

        assertResult(expectedString);
    }

    private void assertResult(String expectedString) {
        final String string = new String(out.array(), out.arrayOffset(), out.readableBytes(), US_ASCII);
        assertThat(string).isEqualTo(expectedString);
    }
}
