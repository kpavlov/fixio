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
package fixio;

import fixio.events.LogonEvent;
import fixio.fixprotocol.*;
import fixio.handlers.FixApplicationAdapter;
import fixio.netty.pipeline.server.AcceptAllAuthenticator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static fixio.fixprotocol.FieldType.*;
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
        server = new FixServer(PORT, new AcceptAllAuthenticator(), new ServerLogicHandler());
        server.start();
    }

    @AfterClass
    public static void afterClass() {
        server.stop();
    }

    private static FixMessageBuilder createUserStatusRequest() {
        FixMessageBuilder userRequest = new FixMessageBuilderImpl(MessageTypes.USER_REQUEST);
        userRequest.add(UserRequestID, "UserRequestID");
        userRequest.add(UserRequestType, 4);//UserRequestType=RequestIndividualUserStatus
        userRequest.add(DataType.STRING, 553, "user");//Username(553)
        return userRequest;
    }

    private static FixMessageBuilder createUserStatusReport() {
        FixMessageBuilder userRequest = new FixMessageBuilderImpl(MessageTypes.USER_RESPONSE);
        userRequest.add(UserRequestID, "UserRequestID");//UserRequestID
        userRequest.add(Username, "user");//553 Username
        userRequest.add(UserStatus, 1);
        userRequest.add(UserStatusText, "Active");
        return userRequest;
    }

    @Before
    public void beforeMethod() throws InterruptedException {
        client = new FixClient(new ClientApp());
        client.setSettingsResource("/fixClient.properties");
        clientCloseFuture = client.connect("127.0.0.1", PORT);
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
        assertEquals(MessageTypes.USER_REQUEST, conversation.get(0).getMessageType());
        assertEquals(MessageTypes.USER_RESPONSE, conversation.get(1).getMessageType());
    }

    private static class ServerLogicHandler extends FixApplicationAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            super.channelRead(ctx, msg);
        }

        @Override
        public void onMessage(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
            if (MessageTypes.USER_REQUEST.equals(msg.getMessageType())) {
                conversation.add(msg);
                ctx.writeAndFlush(createUserStatusReport());
            }
        }
    }

    private class ClientApp extends FixApplicationAdapter {

        @Override
        public void onMessage(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws Exception {
            if ("BF".equals(msg.getMessageType())) {
                conversation.add(msg);
                client.disconnect();
            }
        }

        @Override
        public void onLogon(ChannelHandlerContext ctx, LogonEvent msg) {
            ctx.writeAndFlush(createUserStatusRequest());
        }
    }
}
