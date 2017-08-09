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


public interface FixSessionSettingsProvider {
    String getSenderCompID();
    String getSenderSubID();
    String getSenderLocationID();

    String getTargetCompID();
    String getTargetSubID();
    String getTargetLocationID();

    String getBeginString();

    boolean isResetMsgSeqNum();
    int getHeartbeatInterval();
    String getTimeStampPrecision(); // Valid values are "SECONDS", "MILLIS", "MICROS", "NANOS". Default is "MILLIS"

    String getProperty(String key, String defaultValue);

    default boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, ""+defaultValue));
    }

    final class Params {
        public static final String SSL = "ssl";
    }
}
