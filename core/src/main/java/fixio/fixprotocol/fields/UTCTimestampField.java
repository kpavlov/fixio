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
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.joda.time.DateTimeZone.UTC;

public class UTCTimestampField extends AbstractField<Long> {

    private static final DateTimeFormatter FORMATTER_WITH_MILLIS = DateTimeFormat.forPattern("yyyyMMdd-HH:mm:ss.SSS").withZone(UTC);
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyyMMdd-HH:mm:ss").withZone(UTC);

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
        return new LocalDate(year, month, date).toDateTime(new LocalTime(hour, minute, second, millisecond), UTC).getMillis();
    }

    static long parse(String timestampString) throws ParseException {
        return FORMATTER_WITH_MILLIS.parseDateTime(timestampString).getMillis();
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

    @Override
    public byte[] getBytes() {
        return new DateTime(value).toString(value % 1000 != 0 ? FORMATTER_WITH_MILLIS : FORMATTER).getBytes(US_ASCII);
    }

}
