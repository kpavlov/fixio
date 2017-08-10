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

import fixio.fixprotocol.FixConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFixSessionSettingsProviderImpl implements FixSessionSettingsProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyFixSessionSettingsProviderImpl.class);
    private final Properties properties;

    public PropertyFixSessionSettingsProviderImpl(Properties properties) {
        this.properties = properties;
    }

    public PropertyFixSessionSettingsProviderImpl(String resource) {
        this(new Properties());
        loadProperties(resource);
    }

    public Properties getProperties() {
        return properties;
    }

    private void loadProperties(String resource) {
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
    public boolean isResetMsgSeqNum() {
        return Boolean.parseBoolean(properties.getProperty("ResetOnLogon", "true"));
    }

    @Override
    public int getHeartbeatInterval() {
        return Integer.parseInt(properties.getProperty("HeartBtInt", "60"));
    }

    @Override
    public String getTimeStampPrecision() {// "SECONDS", "MILLIS", "MICROS", "NANOS". Default is "MILLIS"
        return properties.getProperty("TimeStampPrecision", FixConst.TimeStampPrecision.MILLIS.toString()).trim();
    }

    @Override
    public String getDefaultApplVerID() {
        return properties.getProperty("DefaultApplVerID", null);
    }

    @Override
    public String getDefaultApplExtID() {
        return properties.getProperty("DefaultApplExtID", null);
    }

    @Override
    public String getSenderCompID() {
        return properties.getProperty("SenderCompID","").trim();
    }

    @Override
    public String getSenderSubID() {
        return properties.getProperty("SenderSubID","").trim();
    }

    @Override
    public String getSenderLocationID() {
        return properties.getProperty("SenderLocationID","").trim();
    }

    @Override
    public String getTargetCompID() {
        return properties.getProperty("TargetCompID","").trim();
    }

    @Override
    public String getTargetSubID() {
        return properties.getProperty("TargetSubID","").trim();
    }

    @Override
    public String getTargetLocationID() {
        return properties.getProperty("TargetLocationID","").trim();
    }

    @Override
    public String getBeginString() {
        return properties.getProperty("BeginString");
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
