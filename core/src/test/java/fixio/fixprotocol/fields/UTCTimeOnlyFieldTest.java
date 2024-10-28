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

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(UTCTimeOnlyField.parse(TIMESTAMP_NO_MILLIS.getBytes())).isEqualTo(testDate.toLocalTime());
    }

    @Test
    void parseWithMillis() throws Exception {
        assertThat(UTCTimeOnlyField.parse((TIMESTAMP_WITH_MILLIS.getBytes()))).isEqualTo(testDate.plus(MILLIS, ChronoField.MILLI_OF_DAY.getBaseUnit()).toLocalTime());
    }

    @Test
    void parseWithMicros() throws Exception {
        assertThat(UTCTimeOnlyField.parse((TIMESTAMP_WITH_MICROS.getBytes()))).isEqualTo(testDate.plus(MICROS, ChronoField.MICRO_OF_DAY.getBaseUnit()).toLocalTime());
    }

    @Test
    void parseWithNanos() throws Exception {
        assertThat(UTCTimeOnlyField.parse((TIMESTAMP_WITH_NANOS.getBytes()))).isEqualTo(testDate.plus(NANOS, ChronoField.NANO_OF_DAY.getBaseUnit()).toLocalTime());
    }

    @Test
    void parseWithPicos() throws Exception {
        assertThat(UTCTimeOnlyField.parse((TIMESTAMP_WITH_PICOS.getBytes()))).isEqualTo(testDate.plus(NANOS, ChronoField.NANO_OF_DAY.getBaseUnit()).toLocalTime());
    }

    @Test
    void parseLastMillisecond() throws Exception {
        LocalTime expected = LocalTime.of(23, 59, 59).plus(999, ChronoField.MILLI_OF_DAY.getBaseUnit());
        assertThat(UTCTimeOnlyField.parse(("23:59:59.999".getBytes()))).isEqualTo(expected);
    }

    @Test
    void parseLastMicrosecond() throws Exception {
        LocalTime expected = LocalTime.of(23, 59, 59).plus(999999, ChronoField.MICRO_OF_DAY.getBaseUnit());
        assertThat(UTCTimeOnlyField.parse(("23:59:59.999999".getBytes()))).isEqualTo(expected);
    }

    @Test
    void parseLastNanosecond() throws Exception {
        LocalTime expected = LocalTime.of(23, 59, 59, 999999999);
        assertThat(UTCTimeOnlyField.parse(("23:59:59.999999999".getBytes())).toNanoOfDay()).isEqualTo(expected.toNanoOfDay());
    }

    @Test
    void parseLastPicosecond() throws Exception {
        LocalTime expected = LocalTime.of(23, 59, 59, 999999999);
        assertThat(UTCTimeOnlyField.parse(("23:59:59.999999999999".getBytes())).toNanoOfDay()).isEqualTo(expected.toNanoOfDay());
    }

    @Test
    void createNoMillis() throws Exception {
        int tag = new Random().nextInt();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, TIMESTAMP_NO_MILLIS.getBytes());
        assertThat(field.getValue()).isEqualTo(testDate.toLocalTime());
    }

    @Test
    void createWithMillis() throws Exception {
        int tag = new Random().nextInt();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, TIMESTAMP_WITH_MILLIS.getBytes());
        assertThat(field.getValue()).isEqualTo(testDate.plus(MILLIS, ChronoField.MILLI_OF_DAY.getBaseUnit()).toLocalTime());
    }

    @Test
    void createWithMicros() throws Exception {
        int tag = new Random().nextInt();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, TIMESTAMP_WITH_MICROS.getBytes());
        assertThat(field.getValue()).isEqualTo(testDate.plus(MICROS, ChronoField.MICRO_OF_DAY.getBaseUnit()).toLocalTime());
    }

    @Test
    void createWithNanos() throws Exception {
        int tag = new Random().nextInt();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, TIMESTAMP_WITH_NANOS.getBytes());
        assertThat(field.getValue()).isEqualTo(testDate.plus(NANOS, ChronoField.NANO_OF_DAY.getBaseUnit()).toLocalTime());
    }

    @Test
    void createWithPicos() throws Exception {
        int tag = new Random().nextInt();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, TIMESTAMP_WITH_PICOS.getBytes());
        assertThat(field.getValue()).isEqualTo(testDate.plus(NANOS, ChronoField.NANO_OF_DAY.getBaseUnit()).toLocalTime());
    }

    @Test
    void getBytesNoMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_NO_MILLIS.getBytes();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, bytes);
        assertThat(field.getBytes()).containsExactly(bytes);
    }

    @Test
    void getBytesWithMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_MILLIS.getBytes();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, bytes);
        assertThat(field.getBytes()).containsExactly(bytes);
    }

    @Test
    void getBytesWithMicros() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_MICROS.getBytes();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, bytes);
        assertThat(field.getBytes()).containsExactly(bytes);
    }

    @Test
    void getBytesWithNanos() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_NANOS.getBytes();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, bytes);
        assertThat(field.getBytes()).containsExactly(bytes);
    }

    @Test
    void getBytesWithPicos() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_PICOS.getBytes();
        UTCTimeOnlyField field = new UTCTimeOnlyField(tag, bytes);
        // pico are not supported, expect last 3 digits to be truncated
        byte[] nanosBytes = (TIMESTAMP_WITH_NANOS+"000").getBytes();
        assertThat(field.getBytes()).containsExactly(nanosBytes);
    }
}
