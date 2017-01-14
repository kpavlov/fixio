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
package fixio.netty.pipeline;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Fix clock that uses Java 8 system UTC clock by default,
 * it should be easy to add other time zones clock using
 * Java 8 time API.
 */
public class FixClock {

    private static final FixClock INSTANCE = new FixClock(Clock.systemUTC());

    private final Clock clock;
    private final ZoneId zoneId;

    private FixClock(Clock clock) {
        this.clock = clock;
        zoneId = clock.getZone();
    }

    public static FixClock systemUTC() {
        return INSTANCE;
    }

    public Clock clock() {
        return clock;
    }

    public ZoneId zone() {
        return zoneId;
    }

    public long millis() {
        return clock.millis();
    }
}
