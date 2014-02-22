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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFixSessionSettingsProviderImpl implements FixSessionSettingsProvider, MessageSequenceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyFixSessionSettingsProviderImpl.class);
    private final Properties properties;

    public PropertyFixSessionSettingsProviderImpl(String resource) {
        properties = new Properties();
        load(resource);
    }

    private void load(String resource) {
        try {
            final InputStream inputStream = getClass().getResourceAsStream(resource);
            if (inputStream == null) {
                LOGGER.error("Can't read FixSessionSettings from {}", resource);
                throw new IllegalArgumentException("Resource not found: " + resource);
            }
            properties.load(inputStream);
        } catch (IOException e) {
            String message = "Unable to load FixSessionSettings from " + resource;
            LOGGER.error(message, e);
            throw new IllegalArgumentException(message, e);
        }
    }

    @Override
    public int getMsgOutSeqNum() {
        return Integer.parseInt(properties.getProperty("MsgOutSeqNum", "1"));
    }

    @Override
    public int getMsgInSeqNum() {
        return Integer.parseInt(properties.getProperty("MsgInSeqNum", "1"));
    }

    @Override
    public boolean isResetMsgSeqNum() {
        return Boolean.parseBoolean(properties.getProperty("ResetOnLogon", "true"));
    }

    @Override
    public String getSenderCompID() {
        return properties.getProperty("SenderCompID");
    }

    @Override
    public String getSenderSubID() {
        return properties.getProperty("SenderSubID");
    }

    @Override
    public String getTargetCompID() {
        return properties.getProperty("TargetCompID");
    }

    @Override
    public String getTargetSubID() {
        return properties.getProperty("TargetSubID");
    }

    @Override
    public String getBeginString() {
        return properties.getProperty("BeginString");
    }
}
