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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class UTCTimestampField extends AbstractField<Long> {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final ThreadLocal<DateFormat> FORMAT_WITH_MILLIS_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS", Locale.US);
            sdf.setTimeZone(UTC);
            return sdf;
        }
    };
    private static final ThreadLocal<DateFormat> FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss", Locale.US);
            sdf.setTimeZone(UTC);
            return sdf;
        }
    };
    private static final ThreadLocal<Calendar> CALENDAR_THREAD_LOCAL = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return Calendar.getInstance(UTC);
        }
    };
    private final long value;

    protected UTCTimestampField(int tagNum, byte[] bytes, int offset, int length) throws ParseException {
        super(tagNum);
        this.value = parse(bytes, offset, length);
    }

    protected UTCTimestampField(int tagNum, String timestampString) throws ParseException {
        super(tagNum);
        this.value = parse(timestampString);
    }

    protected UTCTimestampField(int tagNum, long value) {
        super(tagNum);
        this.value = value;
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
        int year = (bytes[offset + 0] - '0') * 1000 + (bytes[offset + 1] - '0') * 100 + (bytes[offset + 2] - '0') * 10 + (bytes[offset + 3] - '0');
        int month = (bytes[offset + 4] - '0') * 10 + (bytes[offset + 5] - '0') - 1;
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
        Calendar calendar = CALENDAR_THREAD_LOCAL.get();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTimeInMillis();
    }

    static long parse(String timestampString) throws ParseException {
        return FORMAT_WITH_MILLIS_THREAD_LOCAL.get().parse(timestampString).getTime();
    }

    static long parse(byte[] bytes) throws ParseException {
        return parse(bytes, 0, bytes.length);
    }

    private static void throwParseException(byte[] bytes, int offset, int length, int errorOffset) throws ParseException {
        throw new ParseException("Unparseable date: " + new String(bytes, offset, length, US_ASCII), errorOffset);
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
