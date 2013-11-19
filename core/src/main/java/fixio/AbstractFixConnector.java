/*
 * Copyright 2013 The FIX.io Project
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

import fixio.handlers.AdminEventHandler;
import fixio.handlers.FixMessageHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

/**
 * AbstractFixConnector is base class for {@link FixClient} and {@link FixServer}.
 *
 * @author Konstantin Pavlov
 */
public abstract class AbstractFixConnector {

    private final AdminEventHandler adminHandler;
    private final FixMessageHandler[] appMessageHandlers;

    static {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    public AbstractFixConnector(AdminEventHandler adminHandler, FixMessageHandler... appMessageHandlers) {
        this.adminHandler = adminHandler;
        this.appMessageHandlers = appMessageHandlers;
    }

    protected FixMessageHandler[] getAppMessageHandlers() {
        return appMessageHandlers;
    }

    protected AdminEventHandler getAdminHandler() {
        return adminHandler;
    }
}
