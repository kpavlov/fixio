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

import fixio.fixprotocol.FixConst;

import java.text.ParseException;
import java.time.LocalTime;

import static fixio.fixprotocol.FixConst.TIME_FORMATTER_MICROS;
import static fixio.fixprotocol.FixConst.TIME_FORMATTER_MILLIS;
import static fixio.fixprotocol.FixConst.TIME_FORMATTER_NANOS;
import static fixio.fixprotocol.FixConst.TIME_FORMATTER_PICOS;
import static fixio.fixprotocol.FixConst.TIME_FORMATTER_SECONDS;
import static fixio.fixprotocol.FixConst.TIME_PATTERN_MICROS_LENGTH;
import static fixio.fixprotocol.FixConst.TIME_PATTERN_MILLIS_LENGTH;
import static fixio.fixprotocol.FixConst.TIME_PATTERN_NANOS_LENGTH;
import static fixio.fixprotocol.FixConst.TIME_PATTERN_PICOS_LENGTH;
import static fixio.fixprotocol.FixConst.TIME_PATTERN_SECONDS_LENGTH;
import static java.nio.charset.StandardCharsets.US_ASCII;


public class UTCTimeOnlyField extends AbstractField<LocalTime> {

    private final LocalTime value;
    private final int valueLen;

    public UTCTimeOnlyField(int tagNum, byte[] bytes) throws ParseException {
        super(tagNum);
        this.value = parse(bytes);
        this.valueLen = bytes.length;
    }

    public UTCTimeOnlyField(int tagNum, String timestampString) throws ParseException {
        super(tagNum);
        this.value = parse(timestampString);
        this.valueLen = timestampString.length();
    }

    public UTCTimeOnlyField(int tagNum, LocalTime value) {
        super(tagNum);
        this.value = value;
        this.valueLen = FixConst.TIME_PATTERN_MILLIS.length();
    }

    @Override
    public LocalTime getValue() {
        return value;
    }

    @Override
    public byte[] getBytes() {
        // most likely scenario
        switch (valueLen) {
            case 8:
                return TIME_FORMATTER_SECONDS.format(value).getBytes(US_ASCII);
            case 12:
                return TIME_FORMATTER_MILLIS.format(value).getBytes(US_ASCII);
            case 15:
                return TIME_FORMATTER_MICROS.format(value).getBytes(US_ASCII);
            case 18:
                return TIME_FORMATTER_NANOS.format(value).getBytes(US_ASCII);
            case 21:
                return TIME_FORMATTER_PICOS.format(value).getBytes(US_ASCII);
            default: // no default, logic continues below
        }
        // try to guess
        if (valueLen > TIME_PATTERN_PICOS_LENGTH) {
            return (TIME_FORMATTER_NANOS.format(value)).getBytes(US_ASCII);
        } else if (valueLen > TIME_PATTERN_NANOS_LENGTH) {
            return TIME_FORMATTER_NANOS.format(value).getBytes(US_ASCII);
        } else if (valueLen > TIME_PATTERN_MICROS_LENGTH) {
            return TIME_FORMATTER_MICROS.format(value).getBytes(US_ASCII);
        } else if (valueLen > TIME_PATTERN_MILLIS_LENGTH) {
            return TIME_FORMATTER_MILLIS.format(value).getBytes(US_ASCII);
        } else {
            return TIME_FORMATTER_SECONDS.format(value).getBytes(US_ASCII);
        }
    }

    public static LocalTime parse(String timestampString) throws ParseException {
        if (timestampString != null) {
            int len = timestampString.length();
            // most likely scenario
            switch (len) {
                case 8:
                    return TIME_FORMATTER_SECONDS.parseLocalTime(timestampString);
                case 12:
                    return TIME_FORMATTER_MILLIS.parseLocalTime(timestampString);
                case 15:
                    return TIME_FORMATTER_MICROS.parseLocalTime(timestampString);
                case 18:
                    return TIME_FORMATTER_NANOS.parseLocalTime(timestampString);
                case 21:
                    return TIME_FORMATTER_PICOS.parseLocalTime(timestampString);
                default: // no default, logic continues below
            }
            // try to guess
            if (len >= TIME_PATTERN_PICOS_LENGTH) {
                return TIME_FORMATTER_PICOS.parseLocalTime(timestampString.substring(0, TIME_PATTERN_NANOS_LENGTH));
            } else if (len > TIME_PATTERN_NANOS_LENGTH) {
                return TIME_FORMATTER_NANOS.parseLocalTime(timestampString.substring(0, TIME_PATTERN_NANOS_LENGTH));
            } else if (len > TIME_PATTERN_MICROS_LENGTH) {
                return TIME_FORMATTER_MICROS.parseLocalTime(timestampString.substring(0, TIME_PATTERN_MICROS_LENGTH));
            } else if (len > TIME_PATTERN_MILLIS_LENGTH) {
                return TIME_FORMATTER_MILLIS.parseLocalTime(timestampString.substring(0, TIME_PATTERN_MILLIS_LENGTH));
            } else {
                return TIME_FORMATTER_SECONDS.parseLocalTime(timestampString.substring(0, TIME_PATTERN_SECONDS_LENGTH));
            }
        }
        throw new ParseException("Time is null", -1);
    }

    public static LocalTime parse(byte[] bytes) throws ParseException {
        return parse(new String(bytes, US_ASCII));
    }
}
