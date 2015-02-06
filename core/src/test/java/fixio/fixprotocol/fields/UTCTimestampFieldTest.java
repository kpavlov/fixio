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

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.util.Random;

import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UTCTimestampFieldTest {

    private static final String TIMESTAMP_WITH_MILLIS = "19980604-08:03:31.537";
    private static final String TIMESTAMP_NO_MILLIS = "19980604-08:03:31";

    private final DateTime testDate = new LocalDate(1998, 6, 4).toDateTime(new LocalTime(8, 3, 31, 0), UTC);

    @Test
    public void testParseNoMillis() throws Exception {
        assertEquals(testDate.getMillis(), UTCTimestampField.parse(TIMESTAMP_NO_MILLIS.getBytes()));
    }

    @Test
    public void testParseWithMillis() throws Exception {
        assertEquals(testDate.withField(DateTimeFieldType.millisOfSecond(), 537).getMillis(), UTCTimestampField.parse((TIMESTAMP_WITH_MILLIS.getBytes())));
    }

    @Test
    public void testCreateNoMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_NO_MILLIS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertEquals(testDate.getMillis(), field.getValue().longValue());
    }

    @Test
    public void testCreateWithMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_MILLIS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertEquals(testDate.withField(DateTimeFieldType.millisOfSecond(), 537).getMillis(), field.getValue().longValue());
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
