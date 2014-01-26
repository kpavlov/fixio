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

/**
 * Field representing Date represented in UTC (Universal Time Coordinated, also known as "GMT") in YYYYMMDD format.
 * <p/>
 * This special-purpose field is paired with UTCTimeOnly to form a proper UTCTimestamp for bandwidth-sensitive messages.
 * <p/>
 * Valid values:
 * YYYY = 0000-9999, MM = 01-12, DD = 01-31.
 * <p/>
 * Example(s): <code>MDEntryDate="20030910"</code>
 */
public class UTCDateOnlyField extends AbstractField<Long> {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private static final ThreadLocal<DateFormat> FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
            sdf.setTimeZone(UTC);
            return sdf;
        }
    };

    private static final ThreadLocal<Calendar> CALENDAR_THREAD_LOCAL = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            Calendar calendar = Calendar.getInstance(UTC);
            calendar.clear(Calendar.HOUR_OF_DAY);
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND);
            return calendar;
        }
    };

    private final long value;

    protected UTCDateOnlyField(int tagNum, byte[] bytes) throws ParseException {
        super(tagNum);
        this.value = parse(bytes);
    }

    protected UTCDateOnlyField(int tagNum, String timestampString) throws ParseException {
        super(tagNum);
        this.value = parse(timestampString);
    }

    protected UTCDateOnlyField(int tagNum, long value) {
        super(tagNum);
        this.value = value;
    }

    static long parse(String timestampString) throws ParseException {
        return FORMAT_THREAD_LOCAL.get().parse(timestampString).getTime();
    }

    static long parse(byte[] bytes) throws ParseException {
        if (bytes.length != 8) {
            throwParseException(bytes, 0);
        }
        int year = (bytes[0] - '0') * 1000 + (bytes[1] - '0') * 100 + (bytes[2] - '0') * 10 + (bytes[3] - '0');
        int month = (bytes[4] - '0') * 10 + (bytes[5] - '0') - 1;
        int date = (bytes[6] - '0') * 10 + (bytes[7] - '0');
        Calendar calendar = CALENDAR_THREAD_LOCAL.get();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, date);
        return calendar.getTimeInMillis();
    }

    private static void throwParseException(byte[] bytes, int errorOffset) throws ParseException {
        throw new ParseException("Unparseable date: " + new String(bytes, US_ASCII), errorOffset);
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
        return FORMAT_THREAD_LOCAL.get().format(new Date(value)).getBytes(US_ASCII);
    }

}
