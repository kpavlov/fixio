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

import fixio.fixprotocol.DataType;
import fixio.fixprotocol.FieldType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static fixio.netty.pipeline.FixClock.systemUTC;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldFactoryTest {

    @Test
    void invalidTagField() {
        String value = randomAlphanumeric(5);
        assertThrows(IllegalArgumentException.class, () ->
                FieldFactory.valueOf(0, value.getBytes(US_ASCII)));
    }

    @Test
    void valueOfString() {
        String value = randomAscii(5);
        StringField field = FieldFactory.valueOf(FieldType.MsgType.tag(), value.getBytes(US_ASCII));

        assertEquals(FieldType.MsgType.tag(), field.getTagNum(), "tagnum");
        assertEquals(value, field.getValue(), "value");
    }

    @Test
    void bigTagNumber() {
        String value = randomAscii(5);
        int tagNum = 100_000;
        StringField field = FieldFactory.valueOf(tagNum, value.getBytes(US_ASCII));

        assertEquals(tagNum, field.getTagNum(), "tagnum");
        assertEquals(value, field.getValue(), "value");
    }

    @Test
    void valueOfChar() {
        char value = randomAscii(1).charAt(0);
        CharField field = FieldFactory.valueOf(FieldType.AdvSide.tag(), new byte[]{(byte) value});

        assertEquals(FieldType.AdvSide.tag(), field.getTagNum(), "tagnum");
        assertEquals(value, field.getValue().charValue(), "value");
        assertEquals(value, field.charValue(), "value");
    }

    @Test
    void valueOfInt() {
        int value = new Random().nextInt(1000);
        IntField field = FieldFactory.valueOf(FieldType.EncryptMethod.tag(), String.valueOf(value).getBytes(US_ASCII));

        assertEquals(FieldType.EncryptMethod.tag(), field.getTagNum(), "tagnum");
        assertEquals(value, field.getValue().intValue(), "value");
        assertEquals(value, field.intValue(), "value");
    }

    @Test
    void valueOfLength() {
        int value = new Random().nextInt(1000);

        IntField field = FieldFactory.valueOf(FieldType.BodyLength.tag(), String.valueOf(value).getBytes(US_ASCII));

        assertEquals(FieldType.BodyLength.tag(), field.getTagNum(), "tagnum");
        assertEquals(value, field.getValue().intValue(), "value");
        assertEquals(value, field.intValue(), "value");
    }

    @Test
    void valueOfSeqNum() {
        int value = new Random().nextInt(1000);

        IntField field = FieldFactory.valueOf(FieldType.RefSeqNum.tag(), String.valueOf(value).getBytes(US_ASCII));

        assertEquals(FieldType.RefSeqNum.tag(), field.getTagNum(), "tagnum");
        assertEquals(value, field.getValue().intValue(), "value");
        assertEquals(value, field.intValue(), "value");
    }

    @Test
    void valueOfNumInGroup() {
        int value = new Random().nextInt(1000);

        IntField field = FieldFactory.valueOf(FieldType.NoMDEntries.tag(), String.valueOf(value).getBytes(US_ASCII));

        assertEquals(FieldType.NoMDEntries.tag(), field.getTagNum(), "tagnum");
        assertEquals(value, field.getValue().intValue(), "value");
        assertEquals(value, field.intValue(), "value");
    }

    @Test
    void valueOfFloat() {
        BigDecimal value = BigDecimal.valueOf(new Random().nextInt()).movePointLeft(5);

        FloatField field = FieldFactory.valueOf(FieldType.SettlCurrFxRate.tag(), value.toPlainString().getBytes(US_ASCII));

        assertEquals(FieldType.SettlCurrFxRate.tag(), field.getTagNum(), "tagnum");
        assertEquals(value.doubleValue(), field.getValue().doubleValue(), 0.0, "value");
        assertEquals(value.floatValue(), field.floatValue(), 0.0, "value");
    }

    @Test
    void valueOfQty() {
        BigDecimal value = BigDecimal.valueOf(new Random().nextInt()).movePointLeft(5);

        FloatField field = FieldFactory.valueOf(FieldType.OrderQty.tag(), value.toPlainString().getBytes(US_ASCII));

        assertEquals(FieldType.OrderQty.tag(), field.getTagNum(), "tagnum");
        assertEquals(value.doubleValue(), field.getValue().doubleValue(), 0.0, "value");
        assertEquals(value.floatValue(), field.floatValue(), 0.0, "value");
    }

    @Test
    void valueOfPrice() {
        BigDecimal value = BigDecimal.valueOf(new Random().nextInt()).movePointLeft(5);

        FloatField field = FieldFactory.valueOf(FieldType.MktBidPx.tag(), value.toPlainString().getBytes(US_ASCII));

        assertEquals(FieldType.MktBidPx.tag(), field.getTagNum(), "tagnum");
        assertEquals(value.doubleValue(), field.getValue().doubleValue(), 0.0, "value");
        assertEquals(value.floatValue(), field.floatValue(), 0.0, "value");
    }

    @Test
    void valueOfBooleanTrue() {
        String value = "Y";
        BooleanField field = FieldFactory.valueOf(FieldType.PossDupFlag.tag(), value.getBytes(US_ASCII));

        assertEquals(FieldType.PossDupFlag.tag(), field.getTagNum(), "tagnum");
        assertSame(Boolean.TRUE, field.getValue(), "value");
        assertTrue(field.booleanValue(), "value");
    }

    @Test
    void valueOfBooleanFalse() {
        String value = "N";
        BooleanField field = FieldFactory.valueOf(FieldType.PossDupFlag.tag(), value.getBytes(US_ASCII));

        assertEquals(FieldType.PossDupFlag.tag(), field.getTagNum(), "tagnum");
        assertSame(Boolean.FALSE, field.getValue(), "value");
        assertFalse(field.booleanValue(), "value");
    }

    @ParameterizedTest
    @ValueSource(strings = {"XXX", "", "-"})
    void failValueOfIncorrectBoolean(String value) {
        assertThrows(IllegalArgumentException.class, () ->
                FieldFactory.valueOf(FieldType.PossDupFlag.tag(), value.getBytes(US_ASCII)));
    }

    @Test
    void valueOfUtcTimestampWithMillis() {
        String value = "19980604-08:03:31.537";
        UTCTimestampField field = FieldFactory.valueOf(FieldType.OrigTime.tag(), value.getBytes(US_ASCII));

        assertEquals(FieldType.OrigTime.tag(), field.getTagNum(), "tagnum");
        assertEquals(ZonedDateTime.of(LocalDate.of(1998, 6, 4), LocalTime.of(8, 3, 31, (int) TimeUnit.MILLISECONDS.toNanos(537)), systemUTC().getZone()).toInstant().toEpochMilli(), field.getValue().toInstant().toEpochMilli(), "value");
    }

    @Test
    void valueOfUtcTimestampNoMillis() {
        String value = "19980604-08:03:31";
        UTCTimestampField field = FieldFactory.valueOf(FieldType.OrigTime.tag(), value.getBytes(US_ASCII));

        assertEquals(FieldType.OrigTime.tag(), field.getTagNum(), "tagnum");
        assertEquals(ZonedDateTime.of(LocalDate.of(1998, 6, 4), LocalTime.of(8, 3, 31), systemUTC().getZone()).toInstant().toEpochMilli(), field.getValue().toInstant().toEpochMilli(), "value");
    }

    @ParameterizedTest
    // JunitParamsRunnerToParameterized conversion not supported
    @ValueSource(strings = {"200303", "20030320", "200303w2"})
    void fromStringValueMonthYear(final String value) {
        final int tag = FieldType.MaturityMonthYear.tag();
        StringField field = FieldFactory.fromStringValue(DataType.MONTHYEAR, tag, value);

        assertEquals(tag, field.getTagNum(), "tagnum");
        assertEquals(value, field.getValue(), "value");
    }

    @ParameterizedTest
    // JunitParamsRunnerToParameterized conversion not supported
    @ValueSource(strings = {"2003-09-10"})
    void fromStringValueLocalMktDate(final String value) {
        final int tag = FieldType.TradeOriginationDate.tag();
        StringField field = FieldFactory.fromStringValue(DataType.LOCALMKTDATE, tag, value);

        assertEquals(tag, field.getTagNum(), "tagnum");
        assertEquals(value, field.getValue(), "value");
    }

    @ParameterizedTest
    // JunitParamsRunnerToParameterized conversion not supported
    @ValueSource(strings = {"Y", "true", "TRUE"})
    void fromStringValueBooleanTrue(final String value) {
        final int tag = FieldType.PossDupFlag.tag();
        BooleanField field = FieldFactory.fromStringValue(DataType.BOOLEAN, tag, value);

        assertEquals(tag, field.getTagNum(), "tagnum");
        assertSame(Boolean.TRUE, field.getValue(), "value");
        assertTrue(field.booleanValue(), "value");
    }

    @ParameterizedTest
    // JunitParamsRunnerToParameterized conversion not supported
    @ValueSource(strings = {"N", "false", "FALSE"})
    void valueStringValueBooleanFalse(final String value) {
        final int tag = FieldType.PossDupFlag.tag();
        BooleanField field = FieldFactory.fromStringValue(DataType.BOOLEAN, tag, value);

        assertEquals(tag, field.getTagNum(), "tagnum");
        assertSame(Boolean.FALSE, field.getValue(), "value");
        assertFalse(field.booleanValue(), "value");
    }
}
