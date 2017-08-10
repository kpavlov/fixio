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

import fixio.fixprotocol.FixConst;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Fix clock that uses Java 8 system UTC clock by default,
 * it should be easy to add other time zones clock using
 * Java 8 time API.
 */
public class FixClock extends Clock {

    private static final FixClock INSTANCE = new FixClock(Clock.system(FixConst.DEFAULT_ZONE_ID));

    private final Clock clock;
    private final ZoneId zoneId;
    private final long initialNanos;
    private final Instant initialInstant;

    private FixClock(Clock clock) {
        this.clock = clock;
        this.zoneId = clock.getZone();
        this.initialInstant = clock.instant();
        this.initialNanos = System.nanoTime();
    }

    public static FixClock systemUTC() {
        return INSTANCE;
    }

    @Override
    public Instant instant() {
        return initialInstant.plusNanos(System.nanoTime() - initialNanos);
    }

    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    @Override
    public FixClock withZone(final ZoneId zone) {
        return new FixClock(clock.withZone(zone));
    }

    public ZonedDateTime now() {
        return ZonedDateTime.now(this);
    }
}
