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
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static fixio.netty.pipeline.FixClock.systemUTC;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UTCTimestampFieldTest {

    private static final String TIMESTAMP_WITH_MILLIS = "19980604-08:03:31.537";
    private static final String TIMESTAMP_NO_MILLIS = "19980604-08:03:31";

    private final LocalDate testDate = LocalDate.of(1998, 6, 4);

    private final long testDateTime = ZonedDateTime.of(testDate, LocalTime.of(8, 3, 31), systemUTC().zone()).toInstant().toEpochMilli();
    private final long testDateTimeWithMillis = ZonedDateTime.of(testDate, LocalTime.of(8, 3, 31, (int) TimeUnit.MILLISECONDS.toNanos(537)), systemUTC().zone()).toInstant().toEpochMilli();

    @Test
    public void testParseNoMillis() throws Exception {
        assertEquals(testDateTime, UTCTimestampField.parse(TIMESTAMP_NO_MILLIS.getBytes()));
    }

    @Test
    public void testParseWithMillis() throws Exception {
        assertEquals(testDateTimeWithMillis, UTCTimestampField.parse((TIMESTAMP_WITH_MILLIS.getBytes())));
    }

    @Test
    public void testCreateNoMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_NO_MILLIS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertEquals(testDateTime, field.getValue().longValue());
    }

    @Test
    public void testCreateWithMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_MILLIS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertEquals(testDateTimeWithMillis, field.getValue().longValue());
    }

    @Test
    public void testGetBytesWithMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_MILLIS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertArrayEquals(bytes, field.getBytes());
    }

    @Test
    public void testGetBytesNoMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_NO_MILLIS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertArrayEquals(bytes, field.getBytes());
    }
}
