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
package fixio.netty.pipeline.server;

import fixio.events.LogonEvent;
import fixio.fixprotocol.*;
import fixio.fixprotocol.session.FixSession;
import fixio.netty.AttributeMock;
import fixio.netty.pipeline.AbstractSessionHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerSessionHandlerTest {

    private ServerSessionHandler handler;
    @Mock
    private FixAuthenticator authenticator;
    @Mock
    private ChannelHandlerContext ctx;
    @Captor
    private ArgumentCaptor<FixMessage> messageCaptor;
    private FixMessage logonMsg;
    private List<Object> outgoingMessages;

    @Before
    public void setUp() {
        handler = new ServerSessionHandler(authenticator);
        outgoingMessages = new ArrayList<>();

        logonMsg = new SimpleFixMessage(MessageTypes.LOGON);
        FixMessageHeader header = logonMsg.getHeader();
        header.setMsgSeqNum(1);
        header.setSenderCompID(randomAscii(3));
        header.setTargetCompID(randomAscii(4));

        AttributeKey<FixSession> fixSessionKey = AbstractSessionHandler.FIX_SESSION_KEY;
        Attribute<FixSession> sessionAttribute = new AttributeMock<>();
        when(ctx.attr(fixSessionKey)).thenReturn(sessionAttribute);
    }

    @Test
    public void testLogonSuccess() throws Exception {
        when(authenticator.authenticate(same(logonMsg))).thenReturn(true);

        handler.decode(ctx, logonMsg, outgoingMessages);

        assertEquals(1, outgoingMessages.size());
        assertTrue(outgoingMessages.get(0) instanceof LogonEvent);

        verify(ctx, atLeastOnce()).attr(AbstractSessionHandler.FIX_SESSION_KEY);
        verify(ctx).write(messageCaptor.capture());
        verify(ctx).flush();
        verifyNoMoreInteractions(ctx);

        FixMessage logonAck = messageCaptor.getValue();
        assertLogonAck(logonAck);
    }

    @Test
    public void testAuthenticationFailed() throws Exception {
        when(authenticator.authenticate(same(logonMsg))).thenReturn(false);

        handler.decode(ctx, logonMsg, outgoingMessages);

        verify(ctx).attr(AbstractSessionHandler.FIX_SESSION_KEY);
        verify(ctx).close();
        verifyNoMoreInteractions(ctx);
    }

    @Test
    public void testSequenceTooHigh() throws Exception {
        when(authenticator.authenticate(same(logonMsg))).thenReturn(true);
        logonMsg.getHeader().setMsgSeqNum(2);

        handler.decode(ctx, logonMsg, outgoingMessages);

        assertEquals(1, outgoingMessages.size());
        assertTrue(outgoingMessages.get(0) instanceof LogonEvent);

        verify(ctx, atLeastOnce()).attr(AbstractSessionHandler.FIX_SESSION_KEY);
        verify(ctx, times(2)).write(messageCaptor.capture());
        verify(ctx).flush();
        verifyNoMoreInteractions(ctx);

        final List<FixMessage> sentMessages = messageCaptor.getAllValues();
        assertLogonAck(sentMessages.get(0));
        assertResendRequest(sentMessages.get(1));
    }

    @Test
    public void testSequenceNumberTooLow() throws Exception {
        when(authenticator.authenticate(same(logonMsg))).thenReturn(true);
        logonMsg.getHeader().setMsgSeqNum(0);
        ChannelFuture channelFeature = mock(ChannelFuture.class);
        when(ctx.writeAndFlush(any())).thenReturn(channelFeature);


        handler.decode(ctx, logonMsg, outgoingMessages);

        assertEquals(0, outgoingMessages.size());

        verify(ctx, atLeastOnce()).attr(AbstractSessionHandler.FIX_SESSION_KEY);
        verify(ctx).writeAndFlush(messageCaptor.capture());
        verify(channelFeature).addListener(ChannelFutureListener.CLOSE);
        verifyNoMoreInteractions(ctx);

        assertLogout(messageCaptor.getValue());
    }

    private void assertLogout(FixMessage fixMessage) {
        assertEquals("Logout message type", MessageTypes.LOGOUT, fixMessage.getMessageType());
    }

    private void assertResendRequest(FixMessage fixMessage) {
        assertEquals("ResendRequest message type", MessageTypes.RESEND_REQUEST, fixMessage.getMessageType());
    }

    private void assertLogonAck(FixMessage logonAck) {
        assertEquals("logon ack message type", MessageTypes.LOGON, logonAck.getMessageType());
        assertNull("Username not expected in response.", logonAck.getString(FieldType.Username));
        assertNull("Password not expected in response", logonAck.getString(FieldType.Password));
        assertNotNull("Heartbeat interval", logonAck.getInt(FieldType.HeartBtInt));
        assertEquals("message SeqNum", 1, logonAck.getHeader().getMsgSeqNum());
    }
}
