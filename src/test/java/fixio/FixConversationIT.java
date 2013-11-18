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
package fixio;

import fixio.events.LogonEvent;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.SimpleFixMessage;
import fixio.handlers.AdminEventHandlerAdapter;
import fixio.handlers.FixMessageHandlerAdapter;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FixConversationIT {

    public static final int TEST_TIMEOUT = 5000;
    public static final int PORT = 10453;
    private static FixServer server;
    private static List<FixMessage> conversation = new ArrayList<>();
    private FixClient client;
    private ChannelFuture clientCloseFuture;

    @BeforeClass
    public static void beforeClass() throws InterruptedException {
        server = new FixServer(PORT, new ServerLogicHandler());
        server.start();
    }

    @AfterClass
    public static void afterClass() {
        server.stop();
    }

    private static FixMessage createUserStatusRequest() {
        FixMessage userRequest = new SimpleFixMessage("BE");
        userRequest.add(923, "UserRequestID");//UserRequestID
        userRequest.add(924, 4);//UserRequestType=RequestIndividualUserStatus
        userRequest.add(553, "user");//553 Username
        return userRequest;
    }

    private static FixMessage createUserStatusReport() {
        FixMessage userRequest = new SimpleFixMessage("BF");
        userRequest.add(923, "UserRequestID");//UserRequestID
        userRequest.add(553, "user");//553 Username
        userRequest.add(926, 1);
        userRequest.add(927, "Active");
        return userRequest;
    }

    @Before
    public void beforeMethod() throws InterruptedException {
        client = new FixClient(new ClientLogonHandler(), new ClientLogicHandler());
        clientCloseFuture = client.connect(PORT);
        conversation.clear();
    }

    @After
    public void afterMethod() throws InterruptedException {
        client.disconnect();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testBusinessMessage() throws InterruptedException {

        clientCloseFuture.sync();

        assertEquals(2, conversation.size());
        assertEquals("BE", conversation.get(0).getMessageType());
        assertEquals("BF", conversation.get(1).getMessageType());
    }

    private static class ServerLogicHandler extends FixMessageHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            super.channelRead(ctx, msg);
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
            if ("BE".equals(msg.getMessageType())) {
                conversation.add(msg);
                ctx.writeAndFlush(createUserStatusReport());
            }
        }
    }

    private class ClientLogicHandler extends FixMessageHandlerAdapter {

        @Override
        protected void decode(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
            if ("BF".equals(msg.getMessageType())) {
                conversation.add(msg);
                client.disconnect();
            }
        }
    }

    private static class ClientLogonHandler extends AdminEventHandlerAdapter {

        @Override
        protected void onLogon(ChannelHandlerContext ctx, LogonEvent msg) {
            ctx.writeAndFlush(createUserStatusRequest());
        }
    }
}
