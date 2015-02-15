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

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static fixio.netty.pipeline.FixClock.systemUTC;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class UTCTimestampField extends AbstractTemporalField {

    private static final DateTimeFormatter FORMATTER_WITH_MILLIS = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss.SSS").withZone(systemUTC().zone());
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss").withZone(systemUTC().zone());

    protected UTCTimestampField(int tagNum, byte[] bytes, int offset, int length) throws ParseException {
        super(tagNum, parse(bytes, offset, length));
    }

    protected UTCTimestampField(int tagNum, String timestampString) throws ParseException {
        super(tagNum, parse(timestampString));
    }

    protected UTCTimestampField(int tagNum, long value) {
        super(tagNum, value);
    }

    static long parse(byte[] bytes, int offset, int length) throws ParseException {
        if (length != 21 && length != 17) {
            throwParseException(bytes, offset, length, offset);
        }
        if (bytes[offset + 8] != '-') {
            throwParseException(bytes, offset, length, offset + 8);
        }
        if (bytes[offset + 11] != ':') {
            throwParseException(bytes, offset, length, offset + 11);
        }
        if (bytes[offset + 14] != ':') {
            throwParseException(bytes, offset, length, offset + 14);
        }
        int year = (bytes[offset] - '0') * 1000 + (bytes[offset + 1] - '0') * 100 + (bytes[offset + 2] - '0') * 10 + (bytes[offset + 3] - '0');
        int month = (bytes[offset + 4] - '0') * 10 + (bytes[offset + 5] - '0');
        int date = (bytes[offset + 6] - '0') * 10 + (bytes[offset + 7] - '0');
        int hour = (bytes[offset + 9] - '0') * 10 + (bytes[offset + 10] - '0');
        int minute = (bytes[offset + 12] - '0') * 10 + (bytes[offset + 13] - '0');
        int second = (bytes[offset + 15] - '0') * 10 + (bytes[offset + 16] - '0');
        int millisecond = 0;
        if (length > 17) {
            if (bytes[offset + 17] != '.') {
                throwParseException(bytes, offset, length, offset + 17);
            }
            millisecond = (bytes[offset + 18] - '0') * 100 + (bytes[offset + 19] - '0') * 10 + (bytes[offset + 20] - '0');
        }
        return ZonedDateTime.of(
                LocalDate.of(year, month, date),
                LocalTime.of(hour, minute, second, (int) TimeUnit.MILLISECONDS.toNanos(millisecond)), systemUTC().zone()
        ).toInstant().toEpochMilli();
    }

    static long parse(String timestampString) throws ParseException {
        return ZonedDateTime.parse(timestampString, FORMATTER_WITH_MILLIS).toInstant().toEpochMilli();
    }

    static long parse(byte[] bytes) throws ParseException {
        return parse(bytes, 0, bytes.length);
    }

    private static void throwParseException(byte[] bytes, int offset, int length, int errorOffset) throws ParseException {
        throw new ParseException("Unparseable date: " + new String(bytes, offset, length, US_ASCII), errorOffset);
    }

    @Override
    public byte[] getBytes() {
        return (value % 1000 != 0 ? FORMATTER_WITH_MILLIS.format(Instant.ofEpochMilli(value)) : FORMATTER.format(Instant.ofEpochMilli(value))).getBytes(US_ASCII);
    }
}
