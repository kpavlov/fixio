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
package fixio.fixprotocol.fields;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

import static fixio.netty.pipeline.FixClock.systemUTC;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UTCDateOnlyFieldTest {

    private static final String DATE_STR = "19980604";

    private final LocalDate testDate = LocalDate.of(1998, 6, 4);

    @Test
    public void testParse() throws Exception {
        final ZoneId zone = systemUTC().zone();
        final long expected = ZonedDateTime.of(testDate, LocalTime.MIDNIGHT, zone).toInstant().toEpochMilli();
        assertEquals(expected, UTCDateOnlyField.parse((DATE_STR.getBytes())));
    }

    @Test
    public void testCreate() throws Exception {
        int tag = new Random().nextInt();
        UTCDateOnlyField field = new UTCDateOnlyField(tag, DATE_STR.getBytes());
        final ZoneId zone = systemUTC().zone();
        assertEquals(ZonedDateTime.of(testDate, LocalTime.MIDNIGHT, zone).toInstant().toEpochMilli(), field.getValue().longValue());
    }

    @Test
    public void testGetBytes() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = DATE_STR.getBytes();
        UTCDateOnlyField field = new UTCDateOnlyField(tag, bytes);
        assertArrayEquals(bytes, field.getBytes());
    }
}
