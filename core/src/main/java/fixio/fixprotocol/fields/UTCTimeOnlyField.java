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

import static java.nio.charset.StandardCharsets.US_ASCII;


public class UTCTimeOnlyField extends AbstractField<LocalTime> implements FixConst {

    private final LocalTime value;
    private final int valueLen;

    protected UTCTimeOnlyField(int tagNum, byte[] bytes) throws ParseException {
        super(tagNum);
        this.value = parse(bytes);
        this.valueLen = bytes.length;
    }

    protected UTCTimeOnlyField(int tagNum, String timestampString) throws ParseException {
        super(tagNum);
        this.value = parse(timestampString);
        this.valueLen = timestampString.length();
    }

    protected UTCTimeOnlyField(int tagNum, LocalTime value) {
        super(tagNum);
        this.value = value;
        this.valueLen = TIME_PATTERN_MILLIS.length();
    }

    @Override
    public LocalTime getValue() {
        return value;
    }

    @Override
    public byte[] getBytes() {
        // most likely scenario
        switch (valueLen){
            case 8 : return TIME_FORMATTER_SECONDS.format(value).getBytes(US_ASCII);
            case 12: return TIME_FORMATTER_MILLIS.format(value).getBytes(US_ASCII);
            case 15: return TIME_FORMATTER_MICROS.format(value).getBytes(US_ASCII);
            case 18: return TIME_FORMATTER_NANOS.format(value).getBytes(US_ASCII);
        }
        // try to guess
        if(valueLen>18){
            return TIME_FORMATTER_NANOS.format(value).getBytes(US_ASCII);
        }else if(valueLen>15){
            return TIME_FORMATTER_MICROS.format(value).getBytes(US_ASCII);
        }else if(valueLen>12){
            return TIME_FORMATTER_MILLIS.format(value).getBytes(US_ASCII);
        }else{
            return TIME_FORMATTER_SECONDS.format(value).getBytes(US_ASCII);
        }
    }

    static LocalTime parse(String timestampString) throws ParseException {
        if(timestampString!=null){
            int len = timestampString.length();
            // most likely scenario
            switch (len){
                case 8: return LocalTime.parse(timestampString,TIME_FORMATTER_SECONDS);
                case 12: return LocalTime.parse(timestampString,TIME_FORMATTER_MILLIS);
                case 15: return LocalTime.parse(timestampString,TIME_FORMATTER_MICROS);
                case 18: return LocalTime.parse(timestampString,TIME_FORMATTER_NANOS);
            }
            // try to guess
            if(len>18){
                return LocalTime.parse(timestampString.substring(0,27),TIME_FORMATTER_NANOS);
            }else if(len>15){
                return LocalTime.parse(timestampString.substring(0,24),TIME_FORMATTER_MICROS);
            }else if(len>12){
                return LocalTime.parse(timestampString.substring(0,21),TIME_FORMATTER_MILLIS);
            }else{
                return LocalTime.parse(timestampString.substring(0,17),TIME_FORMATTER_SECONDS);
            }
        }
        throw new ParseException("Unparseable date: '"+timestampString+"'",-1);
    }

    static LocalTime parse(byte[] bytes) throws ParseException {
        return parse(new String(bytes,US_ASCII));
    }
}
