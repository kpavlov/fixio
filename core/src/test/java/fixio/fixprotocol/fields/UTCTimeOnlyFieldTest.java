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

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UTCTimeOnlyFieldTest {

    private static final String TIMESTAMP_WITH_MILLIS = "08:03:31.537";
    private static final String TIMESTAMP_NO_MILLIS = "08:03:31";
    private static final long MILLIS_PER_SECOND = 1000L;
    private static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    private static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    private Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    @Before
    public void setUp() {
        utcCalendar.set(Calendar.YEAR, 1970);
        utcCalendar.set(Calendar.MONTH, 0);
        utcCalendar.set(Calendar.DAY_OF_MONTH, 1);
        utcCalendar.set(Calendar.HOUR_OF_DAY, 8);
        utcCalendar.set(Calendar.MINUTE, 3);
        utcCalendar.set(Calendar.SECOND, 31);
        utcCalendar.clear(Calendar.MILLISECOND);
    }

    @Test
    public void testParseNoMillis() throws Exception {
        assertEquals(utcCalendar.getTimeInMillis(), UTCTimeOnlyField.parse(TIMESTAMP_NO_MILLIS.getBytes()));
    }

    @Test
    public void testParseWithMillis() throws Exception {
        utcCalendar.set(Calendar.MILLISECOND, 537);
        assertEquals(utcCalendar.getTimeInMillis(), UTCTimeOnlyField.parse((TIMESTAMP_WITH_MILLIS.getBytes())));
    }

    @Test
    public void testParseLastMillisecond() throws Exception {
        long lastMillisecondOfDay = MILLIS_PER_HOUR * 24 - 1;
        assertEquals(lastMillisecondOfDay, UTCTimeOnlyField.parse(("23:59:59.999".getBytes())));
    }

    @Test
    public void testCreateNoMillis() throws Exception {
        int tag = new Random().nextInt();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, TIMESTAMP_NO_MILLIS.getBytes());
        assertEquals(utcCalendar.getTimeInMillis(), field.getValue().longValue());
        assertEquals(utcCalendar.getTimeInMillis(), field.timestampMillis());
    }

    @Test
    public void testCreateWithMillis() throws Exception {
        int tag = new Random().nextInt();
        utcCalendar.set(Calendar.MILLISECOND, 537);
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, TIMESTAMP_WITH_MILLIS.getBytes());
        assertEquals(utcCalendar.getTimeInMillis(), field.getValue().longValue());
        assertEquals(utcCalendar.getTimeInMillis(), field.timestampMillis());
    }

    @Test
    public void testGetBytesWithMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_MILLIS.getBytes();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, bytes);

        assertArrayEquals(bytes, field.getBytes());
    }

    @Test
    public void testGetBytesNoMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_NO_MILLIS.getBytes();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, bytes);

        assertArrayEquals(bytes, field.getBytes());
    }
}
