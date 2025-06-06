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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class StatelessMessageSequenceProviderTest {

    private static final StatelessMessageSequenceProvider SEQUENCE_PROVIDER = StatelessMessageSequenceProvider.getInstance();

    @Test
    void getMsgOutSeqNum() {
        assertThat(SEQUENCE_PROVIDER.getMsgInSeqNum()).isEqualTo(1);
    }

    @Test
    void getMsgInSeqNum() {
        assertThat(SEQUENCE_PROVIDER.getMsgOutSeqNum()).isEqualTo(1);
    }
}
