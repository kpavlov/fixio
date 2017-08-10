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
import java.time.ZonedDateTime;

import static fixio.fixprotocol.FixConst.DATE_TIME_FORMATTER_MICROS;
import static fixio.fixprotocol.FixConst.DATE_TIME_FORMATTER_MILLIS;
import static fixio.fixprotocol.FixConst.DATE_TIME_FORMATTER_NANOS;
import static fixio.fixprotocol.FixConst.DATE_TIME_FORMATTER_PICOS;
import static fixio.fixprotocol.FixConst.DATE_TIME_FORMATTER_SECONDS;
import static fixio.fixprotocol.FixConst.DATE_TIME_PATTERN_MICROS_LENGTH;
import static fixio.fixprotocol.FixConst.DATE_TIME_PATTERN_MILLIS;
import static fixio.fixprotocol.FixConst.DATE_TIME_PATTERN_MILLIS_LENGTH;
import static fixio.fixprotocol.FixConst.DATE_TIME_PATTERN_NANOS_LENGTH;
import static fixio.fixprotocol.FixConst.DATE_TIME_PATTERN_PICOS_LENGTH;
import static fixio.fixprotocol.FixConst.DATE_TIME_PATTERN_SECONDS_LENGTH;
import static java.nio.charset.StandardCharsets.US_ASCII;


public class UTCTimestampField extends AbstractField<ZonedDateTime> {

    private final ZonedDateTime value;
    private final int valueLen;


    public UTCTimestampField(int tagNum, byte[] bytes, int offset, int length) throws ParseException {
        super(tagNum);
        this.value = parse(bytes, offset, length);
        this.valueLen = length;
    }

    public UTCTimestampField(int tagNum, String timestampString) throws ParseException {
        super(tagNum);
        this.value = parse(timestampString);
        this.valueLen = timestampString.length();
    }

    public UTCTimestampField(int tagNum, ZonedDateTime value) {
        super(tagNum);
        this.value = value;
        this.valueLen = DATE_TIME_PATTERN_MILLIS.length();
    }

    @Override
    public ZonedDateTime getValue() {
        return value;
    }

    @Override
    public byte[] getBytes() {
        // most likely scenario
        switch (valueLen) {
            case 17:
                return DATE_TIME_FORMATTER_SECONDS.format(value).getBytes(US_ASCII);
            case 21:
                return DATE_TIME_FORMATTER_MILLIS.format(value).getBytes(US_ASCII);
            case 24:
                return DATE_TIME_FORMATTER_MICROS.format(value).getBytes(US_ASCII);
            case 27:
                return DATE_TIME_FORMATTER_NANOS.format(value).getBytes(US_ASCII);
            case 30:
                return DATE_TIME_FORMATTER_PICOS.format(value).getBytes(US_ASCII);
            default:
                // try to guess
                if (valueLen > DATE_TIME_PATTERN_PICOS_LENGTH) {
                    return DATE_TIME_FORMATTER_PICOS.format(value).getBytes(US_ASCII);
                } else if (valueLen > DATE_TIME_PATTERN_NANOS_LENGTH) {
                    return DATE_TIME_FORMATTER_NANOS.format(value).getBytes(US_ASCII);
                } else if (valueLen > DATE_TIME_PATTERN_MICROS_LENGTH) {
                    return DATE_TIME_FORMATTER_MICROS.format(value).getBytes(US_ASCII);
                } else if (valueLen > DATE_TIME_PATTERN_MILLIS_LENGTH) {
                    return DATE_TIME_FORMATTER_MILLIS.format(value).getBytes(US_ASCII);
                } else {
                    return DATE_TIME_FORMATTER_SECONDS.format(value).getBytes(US_ASCII);
                }
        }
    }

    public static ZonedDateTime parse(byte[] bytes) throws ParseException {
        return parse(new String(bytes, US_ASCII));
    }

    public static ZonedDateTime parse(byte[] bytes, int offset, int length) throws ParseException {
        return parse(new String(bytes, offset, length, US_ASCII));
    }

    public static ZonedDateTime parse(String timestampString) throws ParseException {
        if (timestampString != null) {
            int len = timestampString.length();
            // most likely scenario
            switch (len) {
                case 17:
                    return DATE_TIME_FORMATTER_SECONDS.parseZonedDateTime(timestampString);
                case 21:
                    return DATE_TIME_FORMATTER_MILLIS.parseZonedDateTime(timestampString);
                case 24:
                    return DATE_TIME_FORMATTER_MICROS.parseZonedDateTime(timestampString);
                case 27:
                    return DATE_TIME_FORMATTER_NANOS.parseZonedDateTime(timestampString);
                case 30:
                    return DATE_TIME_FORMATTER_PICOS.parseZonedDateTime(timestampString);
                default:
                    // try to guess
                    if (len > DATE_TIME_PATTERN_PICOS_LENGTH) {
                        return DATE_TIME_FORMATTER_PICOS.parseZonedDateTime(timestampString.substring(0, DATE_TIME_PATTERN_PICOS_LENGTH));
                    } else if (len > DATE_TIME_PATTERN_NANOS_LENGTH) {
                        return DATE_TIME_FORMATTER_NANOS.parseZonedDateTime(timestampString.substring(0, DATE_TIME_PATTERN_NANOS_LENGTH));
                    } else if (len > DATE_TIME_PATTERN_MICROS_LENGTH) {
                        return DATE_TIME_FORMATTER_MICROS.parseZonedDateTime(timestampString.substring(0, DATE_TIME_PATTERN_MICROS_LENGTH));
                    } else if (len > DATE_TIME_PATTERN_MILLIS_LENGTH) {
                        return DATE_TIME_FORMATTER_MILLIS.parseZonedDateTime(timestampString.substring(0, DATE_TIME_PATTERN_MILLIS_LENGTH));
                    } else {
                        return DATE_TIME_FORMATTER_SECONDS.parseZonedDateTime(timestampString.substring(0, DATE_TIME_PATTERN_SECONDS_LENGTH));
                    }
            }

        }
        throw new ParseException("Timestamp is null", -1);
    }
}
