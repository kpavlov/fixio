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

/**
 * {@link MessageSequenceProvider} implementation which always returns '1' for inbound and outbound message sequence numbers,
 * i.e. sequence is always reset on session start.
 */
public class StatelessMessageSequenceProvider implements MessageSequenceProvider {

    private static StatelessMessageSequenceProvider INSTANCE = new StatelessMessageSequenceProvider();

    private StatelessMessageSequenceProvider() {
    }

    public static StatelessMessageSequenceProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public int getMsgOutSeqNum() {
        return 1;
    }

    @Override
    public int getMsgInSeqNum() {
        return 1;
    }
}
