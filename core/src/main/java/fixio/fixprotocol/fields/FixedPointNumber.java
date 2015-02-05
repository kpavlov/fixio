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

/**
 * High performance thread-safe Fixed-Point Number implementation.
 * <p/>
 * This class should be used as a replacement for {@link java.math.BigDecimal} when dealing with monetary data in FIX protocol API.
 * <p/>
 * This implementation is <strong>thread-safe.</strong>
 */
public class FixedPointNumber extends Number {

    /**
     * Negative or positive scaled value decimal value: <code>scaledValue := value * (10^scale)</code>
     */
    private final long scaledValue;
    /**
     * If zero or positive, the scale is the number of digits to the right of the decimal point.
     */
    private final byte scale;

    protected FixedPointNumber(byte[] bytes, int offset, int length) {
        int index = offset;
        long scaled = 0;
        int sign = 1;
        switch (bytes[offset]) {
            case '-':
                sign = -1;
                index++;
                break;
            case '+':
                sign = +1;
                index++;
                break;
            default:
        }
        boolean hasScale = false;
        byte scale = 0;
        for (int i = index; i < offset + length; i++) {
            if (bytes[index] == '.') {
                hasScale = true;
            } else {
                int digit = (bytes[index] - '0');
                scaled = scaled * 10 + digit;
                if (hasScale) {
                    scale++;
                }
            }
            index++;
        }

        scaledValue = scaled * sign;
        this.scale = scale;
    }

    public FixedPointNumber(long scaledValue) {
        this.scaledValue = scaledValue;
        this.scale = 0;
    }

    public FixedPointNumber(long scaledValue, byte scale) {
        this.scaledValue = scaledValue;
        this.scale = scale;
    }

    public FixedPointNumber(double value, int precision) {
        this.scale = (byte) precision;
        long factor = (long) Math.pow(10.0, scale);
        scaledValue = Math.round(value * factor);
    }

    public FixedPointNumber(String s) {
        this(s.getBytes(), 0, s.length());
    }

    @Override
    public int intValue() {
        return (int) (longValue());
    }

    @Override
    public long longValue() {
        if (scale == 0) {
            return scaledValue;
        }
        return scaledValue / ((long) Math.pow(10.0, scale));
    }

    @Override@Deprecated
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue() {
        double factor = Math.pow(10.0, scale);
        return scaledValue / factor;
    }

    public long getScaledValue() {
        return scaledValue;
    }

    public byte getScale() {
        return scale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FixedPointNumber that = (FixedPointNumber) o;

        if (scale != that.scale) return false;
        if (scaledValue != that.scaledValue) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (scaledValue ^ (scaledValue >>> 32));
        result = 31 * result + (int) scale;
        return result;
    }

    private String insertPointBefore(int idx){
        StringBuilder sb = new StringBuilder("0.");
        for (int i=idx ; i<0 ; i++){
            sb.append("0");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        String scaledStr = String.valueOf(scaledValue);
        if (scale == 0) {
            return scaledStr;
        }
        int factor = (int) Math.pow(10.0, scale);
        long beforePoint = scaledValue / factor;

        long afterPoint = Math.abs(scaledValue - beforePoint * factor);
        if (beforePoint == 0 && scaledValue < 0) {
            return "-0." + afterPoint;
        } else {
//            return beforePoint + "." + afterPoint;
            int idx = scaledStr.length()-scale;
            String insertPoint;
            if (idx <= 0){
                insertPoint = insertPointBefore(idx);
                idx = 0;
            } else{
                insertPoint = ".";
            }
            return new StringBuilder(scaledStr).insert(idx,insertPoint).toString();
        }
    }
}
