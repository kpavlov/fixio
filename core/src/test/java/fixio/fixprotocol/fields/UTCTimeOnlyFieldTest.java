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

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UTCTimeOnlyFieldTest {

    public static final int MILLIS = 537;
    public static final int MICROS = 537123;
    public static final int NANOS  = 537123456;
    public static final long PICOS = 537123456987L; // java time does not support picos
    //
    private static final String TIMESTAMP_NO_MILLIS = "08:03:31";
    private static final String TIMESTAMP_WITH_MILLIS = TIMESTAMP_NO_MILLIS+"."+MILLIS;
    private static final String TIMESTAMP_WITH_MICROS = TIMESTAMP_NO_MILLIS+"."+MICROS;
    private static final String TIMESTAMP_WITH_NANOS  = TIMESTAMP_NO_MILLIS+"."+NANOS;
    private static final String TIMESTAMP_WITH_PICOS  = TIMESTAMP_NO_MILLIS+"."+PICOS;

    private final ZonedDateTime testDate = ZonedDateTime.of(LocalDate.of(1970, 1, 1),
            LocalTime.of(8, 3, 31, 0), ZoneId.of("UTC"));

    @Test
    void parseNoMillis() throws Exception {
        assertEquals(testDate.toLocalTime(), UTCTimeOnlyField.parse(TIMESTAMP_NO_MILLIS.getBytes()));
    }

    @Test
    void parseWithMillis() throws Exception {
        assertEquals(testDate.plus(MILLIS, ChronoField.MILLI_OF_DAY.getBaseUnit()).toLocalTime(), UTCTimeOnlyField.parse((TIMESTAMP_WITH_MILLIS.getBytes())));
    }

    @Test
    void parseWithMicros() throws Exception {
        assertEquals(testDate.plus(MICROS, ChronoField.MICRO_OF_DAY.getBaseUnit()).toLocalTime(), UTCTimeOnlyField.parse((TIMESTAMP_WITH_MICROS.getBytes())));
    }

    @Test
    void parseWithNanos() throws Exception {
        assertEquals(testDate.plus(NANOS, ChronoField.NANO_OF_DAY.getBaseUnit()).toLocalTime(), UTCTimeOnlyField.parse((TIMESTAMP_WITH_NANOS.getBytes())));
    }

    @Test
    void parseWithPicos() throws Exception {
        assertEquals(testDate.plus(NANOS, ChronoField.NANO_OF_DAY.getBaseUnit()).toLocalTime(), UTCTimeOnlyField.parse((TIMESTAMP_WITH_PICOS.getBytes())));
    }

    @Test
    void parseLastMillisecond() throws Exception {
        LocalTime expected = LocalTime.of(23, 59, 59).plus(999, ChronoField.MILLI_OF_DAY.getBaseUnit());
        assertEquals(expected, UTCTimeOnlyField.parse(("23:59:59.999".getBytes())));
    }

    @Test
    void parseLastMicrosecond() throws Exception {
        LocalTime expected = LocalTime.of(23, 59, 59).plus(999999, ChronoField.MICRO_OF_DAY.getBaseUnit());
        assertEquals(expected, UTCTimeOnlyField.parse(("23:59:59.999999".getBytes())));
    }

    @Test
    void parseLastNanosecond() throws Exception {
        LocalTime expected = LocalTime.of(23, 59, 59, 999999999);
        assertEquals(expected.toNanoOfDay(), UTCTimeOnlyField.parse(("23:59:59.999999999".getBytes())).toNanoOfDay());
    }

    @Test
    void parseLastPicosecond() throws Exception {
        LocalTime expected = LocalTime.of(23, 59, 59, 999999999);
        assertEquals(expected.toNanoOfDay(), UTCTimeOnlyField.parse(("23:59:59.999999999999".getBytes())).toNanoOfDay());
    }

    @Test
    void createNoMillis() throws Exception {
        int tag = new Random().nextInt();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, TIMESTAMP_NO_MILLIS.getBytes());
        assertEquals(testDate.toLocalTime(), field.getValue());
    }

    @Test
    void createWithMillis() throws Exception {
        int tag = new Random().nextInt();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, TIMESTAMP_WITH_MILLIS.getBytes());
        assertEquals(testDate.plus(MILLIS, ChronoField.MILLI_OF_DAY.getBaseUnit()).toLocalTime(), field.getValue());
    }

    @Test
    void createWithMicros() throws Exception {
        int tag = new Random().nextInt();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, TIMESTAMP_WITH_MICROS.getBytes());
        assertEquals(testDate.plus(MICROS, ChronoField.MICRO_OF_DAY.getBaseUnit()).toLocalTime(), field.getValue());
    }

    @Test
    void createWithNanos() throws Exception {
        int tag = new Random().nextInt();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, TIMESTAMP_WITH_NANOS.getBytes());
        assertEquals(testDate.plus(NANOS, ChronoField.NANO_OF_DAY.getBaseUnit()).toLocalTime(), field.getValue());
    }

    @Test
    void createWithPicos() throws Exception {
        int tag = new Random().nextInt();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, TIMESTAMP_WITH_PICOS.getBytes());
        assertEquals(testDate.plus(NANOS, ChronoField.NANO_OF_DAY.getBaseUnit()).toLocalTime(), field.getValue());
    }

    @Test
    void getBytesNoMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_NO_MILLIS.getBytes();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, bytes);
        assertArrayEquals(bytes, field.getBytes());
    }

    @Test
    void getBytesWithMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_MILLIS.getBytes();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, bytes);
        assertArrayEquals(bytes, field.getBytes());
    }

    @Test
    void getBytesWithMicros() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_MICROS.getBytes();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, bytes);
        assertArrayEquals(bytes, field.getBytes());
    }

    @Test
    void getBytesWithNanos() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_NANOS.getBytes();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, bytes);
        assertArrayEquals(bytes, field.getBytes());
    }

    @Test
    void getBytesWithPicos() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_PICOS.getBytes();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, bytes);
        // pico are not supported, expect last 3 digits to be truncated
        byte[] nanosBytes = (TIMESTAMP_WITH_NANOS+"000").getBytes();
        assertArrayEquals(nanosBytes, field.getBytes());
    }
}
