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
import java.time.ZonedDateTime;

import static java.nio.charset.StandardCharsets.US_ASCII;


public class UTCTimestampField extends AbstractField<ZonedDateTime> implements FixConst {

    private final ZonedDateTime value;
    private final int valueLen;


    public UTCTimestampField(int tagNum, byte[] bytes, int offset, int length) throws ParseException {
        super(tagNum);
        this.value = parse(bytes,offset,length);
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
        switch (valueLen){
            case 17: return DATE_TIME_FORMATTER_SECONDS.format(value).getBytes(US_ASCII);
            case 21: return DATE_TIME_FORMATTER_MILLIS.format(value).getBytes(US_ASCII);
            case 24: return DATE_TIME_FORMATTER_MICROS.format(value).getBytes(US_ASCII);
            case 27: return DATE_TIME_FORMATTER_NANOS.format(value).getBytes(US_ASCII);
        }
        // try to guess
        if(valueLen>27){
            return DATE_TIME_FORMATTER_NANOS.format(value).getBytes(US_ASCII);
        }else if(valueLen>24){
            return DATE_TIME_FORMATTER_MICROS.format(value).getBytes(US_ASCII);
        }else if(valueLen>21){
            return DATE_TIME_FORMATTER_MILLIS.format(value).getBytes(US_ASCII);
        }else{
            return DATE_TIME_FORMATTER_SECONDS.format(value).getBytes(US_ASCII);
        }
    }

    public static ZonedDateTime parse(byte[] bytes) throws ParseException {
        return parse(new String(bytes,US_ASCII));
    }

    public static ZonedDateTime parse(byte[] bytes, int offset, int length) throws ParseException {
        return parse(new String(bytes,offset,length,US_ASCII));
    }

    public static ZonedDateTime parse(String timestampString) throws ParseException {
        if(timestampString!=null){
            int len = timestampString.length();
            // most likely scenario
            switch (len){
                case 17: return ZonedDateTime.parse(timestampString,DATE_TIME_FORMATTER_SECONDS);
                case 21: return ZonedDateTime.parse(timestampString,DATE_TIME_FORMATTER_MILLIS);
                case 24: return ZonedDateTime.parse(timestampString,DATE_TIME_FORMATTER_MICROS);
                case 27: return ZonedDateTime.parse(timestampString,DATE_TIME_FORMATTER_NANOS);
                default: // no default, logic continues below
            }
            // try to guess
            if(len>27){
                return ZonedDateTime.parse(timestampString.substring(0,27),DATE_TIME_FORMATTER_NANOS);
            }else if(len>24){
                return ZonedDateTime.parse(timestampString.substring(0,24),DATE_TIME_FORMATTER_MICROS);
            }else if(len>21){
                return ZonedDateTime.parse(timestampString.substring(0,21),DATE_TIME_FORMATTER_MILLIS);
            }else{
                return ZonedDateTime.parse(timestampString.substring(0,17),DATE_TIME_FORMATTER_SECONDS);
            }
        }
        throw new ParseException("Unparseable date: '"+timestampString+"'",-1);
    }
}
