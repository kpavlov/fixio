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
package fixio.netty.pipeline.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyFixSessionSettingsProviderImplTest {

    private static PropertyFixSessionSettingsProviderImpl provider;

    @BeforeAll
    static void beforeClass() {
        provider = new PropertyFixSessionSettingsProviderImpl("/fixClient.properties");
    }

    @Test
    void getHeartbeatInterval() {
        assertThat(provider.getHeartbeatInterval()).isEqualTo(40);
    }

    @Test
    void getBeginString() {
        assertThat(provider.getBeginString()).isEqualTo("FIX.4.4");
    }

    @Test
    void getSenderCompID() {
        assertThat(provider.getSenderCompID()).isEqualTo("Client.CompID");
    }

    @Test
    void getSenderSubID() {
        assertThat(provider.getSenderSubID()).isEqualTo("Client.SubID");
    }

    @Test
    void getTargetCompID() {
        assertThat(provider.getTargetCompID()).isEqualTo("Server.CompID");
    }

    @Test
    void getTargetSubID() {
        assertThat(provider.getTargetSubID()).isEqualTo("Server.SubID");
    }

    @Test
    void isResetMsgSeqNum() {
        assertThat(provider.isResetMsgSeqNum()).isTrue();
    }

}

