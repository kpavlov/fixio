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

public class UTCTimestampFieldTest {

    private static final String TIMESTAMP_WITH_MILLIS = "19980604-08:03:31.537";
    private static final String TIMESTAMP_NO_MILLIS = "19980604-08:03:31";
    private Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    @Before
    public void setUp() {
        calendar.set(Calendar.YEAR, 1998);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 4);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 3);
        calendar.set(Calendar.SECOND, 31);
        calendar.clear(Calendar.MILLISECOND);
    }

    @Test
    public void testParseNoMillis() throws Exception {
        assertEquals(calendar.getTimeInMillis(), UTCTimestampField.parse(TIMESTAMP_NO_MILLIS.getBytes()));
    }

    @Test
    public void testParseWithMillis() throws Exception {
        calendar.set(Calendar.MILLISECOND, 537);
        assertEquals(calendar.getTimeInMillis(), UTCTimestampField.parse((TIMESTAMP_WITH_MILLIS.getBytes())));
    }

    @Test
    public void testCreateNoMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_NO_MILLIS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertEquals(calendar.getTimeInMillis(), field.getValue().longValue());
        assertEquals(calendar.getTimeInMillis(), field.timestampMillis());
    }

    @Test
    public void testCreateWithMillis() throws Exception {
        int tag = new Random().nextInt();
        calendar.set(Calendar.MILLISECOND, 537);
        byte[] bytes = TIMESTAMP_WITH_MILLIS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertEquals(calendar.getTimeInMillis(), field.getValue().longValue());
        assertEquals(calendar.getTimeInMillis(), field.timestampMillis());
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
