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

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;

import static fixio.netty.pipeline.FixClock.systemUTC;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class UTCTimeOnlyField extends AbstractTemporalField {

    private static final long MILLIS_PER_SECOND = 1000;
    private static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    private static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    private static final DateTimeFormatter FORMATTER_WITH_MILLIS = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(systemUTC().zone());
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(systemUTC().zone());

    protected UTCTimeOnlyField(int tagNum, byte[] bytes) throws ParseException {
        super(tagNum, parse(bytes));
    }

    protected UTCTimeOnlyField(int tagNum, String timestampString) throws ParseException {
        super(tagNum, parse(timestampString));
    }

    protected UTCTimeOnlyField(int tagNum, long value) {
        super(tagNum, value);
    }

    static long parse(String timestampString) throws ParseException {
        final ZoneId zone = systemUTC().zone();
        return ZonedDateTime.of(LocalDate.now(zone), LocalTime.parse(timestampString, FORMATTER), zone).toInstant().toEpochMilli();
    }

    static long parse(byte[] bytes) throws ParseException {
        if (bytes.length != 8 && bytes.length != 12) {
            throwParseException(bytes, 0);
        }
        if (bytes[2] != ':') {
            throwParseException(bytes, 2);
        }
        if (bytes[5] != ':') {
            throwParseException(bytes, 5);
        }

        int hour = (bytes[0] - '0') * 10 + (bytes[1] - '0');
        int minute = (bytes[3] - '0') * 10 + (bytes[4] - '0');
        int second = (bytes[6] - '0') * 10 + (bytes[7] - '0');
        int millisecond = 0;
        if (bytes.length > 8) {
            if (bytes[8] != '.') {
                throwParseException(bytes, 8);
            }
            millisecond = (bytes[9] - '0') * 100 + (bytes[10] - '0') * 10 + (bytes[11] - '0');
        }
        assert (hour >= 0 && hour <= 23) : "Invalid value for hour: " + hour;
        assert (minute >= 0 && minute <= 59) : "Invalid value for minute: " + minute;
        assert (second >= 0 && second <= 59) : "Invalid value for second: " + second;
        assert (millisecond >= 0 && millisecond < 1000) : "Invalid value for millisecond: " + millisecond;

        return MILLIS_PER_HOUR * hour + MILLIS_PER_MINUTE * minute + MILLIS_PER_SECOND * second + millisecond;
    }

    private static void throwParseException(byte[] bytes, int errorOffset) throws ParseException {
        throw new ParseException("Unparseable date: " + new String(bytes, StandardCharsets.US_ASCII), errorOffset);
    }

    @Override
    public byte[] getBytes() {
        return (value % 1000 != 0 ? FORMATTER_WITH_MILLIS.format(Instant.ofEpochMilli(value)) : FORMATTER.format(Instant.ofEpochMilli(value))).getBytes(US_ASCII);
    }

}
