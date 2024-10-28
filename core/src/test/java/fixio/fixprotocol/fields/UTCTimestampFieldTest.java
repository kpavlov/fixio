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

import fixio.netty.pipeline.FixClock;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class UTCTimestampFieldTest {
    public static final int MILLIS = 537;
    public static final int MICROS = 537123;
    public static final int NANOS  = 537123456;
    public static final long PICOS = 537123456987L; // java time does not support picos
    //
    private static final String TIMESTAMP_NO_MILLIS = "19980604-08:03:31";
    private static final String TIMESTAMP_WITH_MILLIS = TIMESTAMP_NO_MILLIS+"."+MILLIS;
    private static final String TIMESTAMP_WITH_MICROS = TIMESTAMP_NO_MILLIS+"."+MICROS;
    private static final String TIMESTAMP_WITH_NANOS  = TIMESTAMP_NO_MILLIS+"."+NANOS;
    private static final String TIMESTAMP_WITH_PICOS  = TIMESTAMP_NO_MILLIS+"."+PICOS;
    //
    private final ZonedDateTime testDate = ZonedDateTime.of(LocalDate.of(1998, 6, 4), LocalTime.of(8, 3, 31), FixClock.systemUTC().getZone());
    private final ZonedDateTime testDateWithMillis = testDate.plus(MILLIS, ChronoField.MILLI_OF_DAY.getBaseUnit());
    private final ZonedDateTime testDateWithMicros = testDate.plus(MICROS, ChronoField.MICRO_OF_DAY.getBaseUnit());
    private final ZonedDateTime testDateWithNanos  = testDate.plusNanos(NANOS);

    @Test
    void parseNoMillis() throws Exception {
        assertThat(UTCTimestampField.parse(TIMESTAMP_NO_MILLIS.getBytes())).isEqualTo(testDate);
    }

    @Test
    void parseWithMillis() throws Exception {
        assertThat(UTCTimestampField.parse((TIMESTAMP_WITH_MILLIS.getBytes()))).isEqualTo(testDateWithMillis);
    }

    @Test
    void parseWithMicros() throws Exception {
        assertThat(UTCTimestampField.parse((TIMESTAMP_WITH_MICROS.getBytes()))).isEqualTo(testDateWithMicros);
    }

    @Test
    void parseWithNanos() throws Exception {
        assertThat(UTCTimestampField.parse((TIMESTAMP_WITH_NANOS.getBytes()))).isEqualTo(testDateWithNanos);
    }

    @Test
    void parseWithPicos() throws Exception {
        // pico are not supported, expect last 3 digits to be truncated
        assertThat(UTCTimestampField.parse((TIMESTAMP_WITH_PICOS.getBytes()))).isEqualTo(testDateWithNanos);
    }

    /// testCreate ////////////////
    @Test
    void createNoMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_NO_MILLIS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertThat(field.getValue()).isEqualTo(testDate);
    }

    @Test
    void createWithMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_MILLIS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertThat(field.getValue()).isEqualTo(testDateWithMillis);
    }

    @Test
    void createWithMicros() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_MICROS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertThat(field.getValue()).isEqualTo(testDateWithMicros);
    }

    @Test
    void createWithNanos() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_NANOS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertThat(field.getValue()).isEqualTo(testDateWithNanos);
    }

    @Test
    void createWithPicos() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_PICOS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        // pico are not supported, expect last 3 digits to be truncated
        assertThat(field.getValue()).isEqualTo(testDateWithNanos);
    }

    /// testGetBytes ////////////////
    @Test
    void getBytesNoMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_NO_MILLIS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertThat(field.getBytes()).containsExactly(bytes);
    }

    @Test
    void getBytesWithMillis() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_MILLIS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertThat(field.getBytes()).containsExactly(bytes);
    }

    @Test
    void getBytesMicros() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_MICROS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertThat(field.getBytes()).containsExactly(bytes);
    }

    @Test
    void getBytesNanos() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_NANOS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        assertThat(field.getBytes()).containsExactly(bytes);
    }

    @Test
    void getBytesPicos() throws Exception {
        int tag = new Random().nextInt();
        byte[] bytes = TIMESTAMP_WITH_PICOS.getBytes();
        UTCTimestampField field = new UTCTimestampField(tag, bytes, 0, bytes.length);
        // pico are not supported, expect last 3 digits to be truncated
        byte[] nanosBytes = (TIMESTAMP_WITH_NANOS+"000").getBytes();
        assertThat(field.getBytes()).containsExactly(nanosBytes);
    }
}
