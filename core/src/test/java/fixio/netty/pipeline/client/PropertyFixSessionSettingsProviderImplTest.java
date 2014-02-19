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

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertyFixSessionSettingsProviderImplTest {

    private static PropertyFixSessionSettingsProviderImpl provider;

    @BeforeClass
    public static void beforeClass() {
        provider = new PropertyFixSessionSettingsProviderImpl("/fixClient.properties");
    }

    @Test
    public void testGetMsgSeqNumber() {
        assertEquals(1, provider.getMsgSeqNum());
    }

    @Test
    public void testGetBeginString() {
        assertEquals("FIX.4.4", provider.getBeginString());
    }

    @Test
    public void testGetSenderCompID() {
        assertEquals("Client.CompID", provider.getSenderCompID());
    }

    @Test
    public void testGetSenderSubID() {
        assertEquals("Client.SubID", provider.getSenderSubID());
    }

    @Test
    public void testGetTargetCompID() {
        assertEquals("Server.CompID", provider.getTargetCompID());
    }

    @Test
    public void testGetTargetSubID() {
        assertEquals("Server.SubID", provider.getTargetSubID());
    }

    @Test
    public void testIsResetMsgSeqNum() {
        assertEquals(true, provider.isResetMsgSeqNum());
    }


}

