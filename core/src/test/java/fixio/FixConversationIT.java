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
import fixio.fixprotocol.DataType;
import fixio.fixprotocol.FixMessage;
import fixio.fixprotocol.FixMessageBuilder;
import fixio.fixprotocol.FixMessageBuilderImpl;
import fixio.fixprotocol.MessageTypes;
import fixio.handlers.FixApplicationAdapter;
import fixio.netty.pipeline.InMemorySessionRepository;
import fixio.netty.pipeline.client.PropertyAuthenticationProvider;
import fixio.netty.pipeline.client.PropertyFixSessionSettingsProviderImpl;
import fixio.netty.pipeline.server.AcceptAllAuthenticator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static fixio.fixprotocol.FieldType.UserRequestID;
import static fixio.fixprotocol.FieldType.UserRequestType;
import static fixio.fixprotocol.FieldType.UserStatus;
import static fixio.fixprotocol.FieldType.UserStatusText;
import static fixio.fixprotocol.FieldType.Username;
import static org.assertj.core.api.Assertions.assertThat;

class FixConversationIT {

    private static final int TEST_TIMEOUT = 5000;
    private static final int PORT = 10453;
    private static final List<FixMessage> conversation = new ArrayList<>();
    private static FixServer server;
    private FixClient client;
    private ChannelFuture clientCloseFuture;

    @BeforeAll
    static void beforeClass() throws InterruptedException {
        server = new FixServer(PORT,
                new ServerLogicHandler(),
                new AcceptAllAuthenticator(),
                new InMemorySessionRepository());
        server.start();
    }

    @AfterAll
    static void afterClass() {
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

    @BeforeEach
    void beforeMethod() throws InterruptedException {
        client = new FixClient(new ClientApp(), new InMemorySessionRepository());
        final PropertyFixSessionSettingsProviderImpl settingsProvider = new PropertyFixSessionSettingsProviderImpl("/fixClient.properties");
        final PropertyAuthenticationProvider authenticationProvider = new PropertyAuthenticationProvider(settingsProvider.getProperties());
        client.setSessionSettingsProvider(settingsProvider);
        client.setAuthenticationProvider(authenticationProvider);
        clientCloseFuture = client.connect("127.0.0.1", PORT);
        conversation.clear();
    }

    @AfterEach
    void afterMethod() throws InterruptedException {
        client.disconnect();
    }

    @Test
    @Timeout(value = TEST_TIMEOUT, unit = TimeUnit.MILLISECONDS)
    void businessMessage() throws InterruptedException {

        clientCloseFuture.sync();

        assertThat(conversation.size()).isEqualTo(2);
        assertThat(conversation.get(0).getMessageType()).isEqualTo(MessageTypes.USER_REQUEST);
        assertThat(conversation.get(1).getMessageType()).isEqualTo(MessageTypes.USER_RESPONSE);
    }

    private static class ServerLogicHandler extends FixApplicationAdapter {

        @Override
        public void onMessage(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) {
            if (MessageTypes.USER_REQUEST.equals(msg.getMessageType())) {
                conversation.add(msg);
                ctx.writeAndFlush(createUserStatusReport());
            }
        }
    }

    private class ClientApp extends FixApplicationAdapter {

        @Override
        public void onMessage(ChannelHandlerContext ctx, FixMessage msg, List<Object> out) throws InterruptedException {
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
