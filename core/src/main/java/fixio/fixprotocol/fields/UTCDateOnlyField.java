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
import java.time.LocalDate;

import static fixio.fixprotocol.FixConst.DATE_FORMATTER;
import static java.nio.charset.StandardCharsets.US_ASCII;


/**
 * Field representing Date represented in UTC (Universal Time Coordinated, also known as "GMT") in YYYYMMDD format.
 * <p>
 * This special-purpose field is paired with UTCTimeOnly to form a proper UTCTimestamp for bandwidth-sensitive messages.
 * </p>
 * <p>
 * Valid values:
 * YYYY = 0000-9999,
 * MM = 01-12,
 * DD = 01-31.
 * </p>
 * Example(s): <code>MDEntryDate="20030910"</code>
 */
public class UTCDateOnlyField extends AbstractField<LocalDate> {

    private final LocalDate value;

    public UTCDateOnlyField(int tagNum, byte[] bytes) throws ParseException {
        super(tagNum);
        this.value = parse(bytes);
    }

    public UTCDateOnlyField(int tagNum, String timestampString) throws ParseException {
        super(tagNum);
        this.value = parse(timestampString);
    }

    public UTCDateOnlyField(int tagNum, LocalDate value) {
        super(tagNum);
        this.value = value;
    }

    @Override
    public byte[] getBytes() {
        return DATE_FORMATTER.format(value).getBytes(US_ASCII);
    }

    @Override
    public LocalDate getValue() {
        return value;
    }

    public static LocalDate parse(String timestampString) throws ParseException {
        if (timestampString != null) {
            int len = timestampString.length();
            if (len < 8) {
                throw new ParseException("Unparseable date: '" + timestampString + "'", 0);
            } else if (len == 8) {
                return DATE_FORMATTER.parseLocalDate(timestampString);
            } else {
                return DATE_FORMATTER.parseLocalDate(timestampString.substring(0, 8));
            }
        }
        throw new ParseException("Date is null", 0);
    }

    public static LocalDate parse(byte[] bytes) throws ParseException {
        return parse(new String(bytes, US_ASCII));
    }
}
