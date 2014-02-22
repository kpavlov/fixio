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

import fixio.handlers.FixApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FixServerTest {

    private FixServer server;
    @Mock
    private FixApplication fixApplication;

    @Before
    public void setUp() throws Exception {
        server = new FixServer(10100, fixApplication);
    }

    @Test
    public void testStartStop() throws Exception {
        server.start();
        server.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void testStopBeforeStart() throws Exception {
        server.stop();
    }
}
