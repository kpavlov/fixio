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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class UTCTimeOnlyField extends AbstractField<Long> {

    private static final long MILLIS_PER_SECOND = 1000;
    private static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    private static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final ThreadLocal<DateFormat> FORMAT_WITH_MILLIS_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
            sdf.setTimeZone(UTC);
            return sdf;
        }
    };
    private static final ThreadLocal<DateFormat> FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
            sdf.setTimeZone(UTC);
            return sdf;
        }
    };
    private final long value;

    protected UTCTimeOnlyField(int tagNum, byte[] bytes) throws ParseException {
        super(tagNum);
        this.value = parse(bytes);
    }

    protected UTCTimeOnlyField(int tagNum, String timestampString) throws ParseException {
        super(tagNum);
        this.value = parse(timestampString);
    }

    protected UTCTimeOnlyField(int tagNum, long value) {
        super(tagNum);
        this.value = value;
    }

    static long parse(String timestampString) throws ParseException {
        return FORMAT_THREAD_LOCAL.get().parse(timestampString).getTime();
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
    public Long getValue() {
        return value;
    }

    public long timestampMillis() {
        return value;
    }

    @Override
    public byte[] getBytes() {
        if (value % 1000 != 0) {
            // with millis
            return FORMAT_WITH_MILLIS_THREAD_LOCAL.get().format(new Date(value)).getBytes(US_ASCII);
        } else {
            return FORMAT_THREAD_LOCAL.get().format(new Date(value)).getBytes(US_ASCII);
        }
    }

}
