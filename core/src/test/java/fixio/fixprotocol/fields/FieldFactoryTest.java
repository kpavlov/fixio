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
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.*;

public class FieldFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTagField() throws Exception {
        String value = randomAlphanumeric(5);
        FieldFactory.valueOf(0, value.getBytes(US_ASCII));
    }

    @Test
    public void testValueOfString() throws Exception {
        String value = randomAscii(5);
        StringField field = FieldFactory.valueOf(FieldType.MsgType.tag(), value.getBytes(US_ASCII));

        assertEquals("tagnum", FieldType.MsgType.tag(), field.getTagNum());
        assertEquals("value", value, field.getValue());
    }

    @Test
    public void testBigTagNumber() throws Exception {
        String value = randomAscii(5);
        int tagNum = 100_000;
        StringField field = FieldFactory.valueOf(tagNum, value.getBytes(US_ASCII));

        assertEquals("tagnum", tagNum, field.getTagNum());
        assertEquals("value", value, field.getValue());
    }

    @Test
    public void testValueOfChar() throws Exception {
        char value = randomAscii(1).charAt(0);
        CharField field = FieldFactory.valueOf(FieldType.AdvSide.tag(), new byte[]{(byte) value});

        assertEquals("tagnum", FieldType.AdvSide.tag(), field.getTagNum());
        assertEquals("value", value, field.getValue().charValue());
        assertEquals("value", value, field.charValue());
    }

    @Test
    public void testValueOfInt() throws Exception {
        int value = new Random().nextInt(1000);
        IntField field = FieldFactory.valueOf(FieldType.EncryptMethod.tag(), String.valueOf(value).getBytes(US_ASCII));

        assertEquals("tagnum", FieldType.EncryptMethod.tag(), field.getTagNum());
        assertEquals("value", value, field.getValue().intValue());
        assertEquals("value", value, field.intValue());
    }

    @Test
    public void testValueOfLength() throws Exception {
        int value = new Random().nextInt(1000);

        IntField field = FieldFactory.valueOf(FieldType.BodyLength.tag(), String.valueOf(value).getBytes(US_ASCII));

        assertEquals("tagnum", FieldType.BodyLength.tag(), field.getTagNum());
        assertEquals("value", value, field.getValue().intValue());
        assertEquals("value", value, field.intValue());
    }

    @Test
    public void testValueOfSeqNum() throws Exception {
        int value = new Random().nextInt(1000);

        IntField field = FieldFactory.valueOf(FieldType.RefSeqNum.tag(), String.valueOf(value).getBytes(US_ASCII));

        assertEquals("tagnum", FieldType.RefSeqNum.tag(), field.getTagNum());
        assertEquals("value", value, field.getValue().intValue());
        assertEquals("value", value, field.intValue());
    }

    @Test
    public void testValueOfNumInGroup() throws Exception {
        int value = new Random().nextInt(1000);

        IntField field = FieldFactory.valueOf(FieldType.NoMDEntries.tag(), String.valueOf(value).getBytes(US_ASCII));

        assertEquals("tagnum", FieldType.NoMDEntries.tag(), field.getTagNum());
        assertEquals("value", value, field.getValue().intValue());
        assertEquals("value", value, field.intValue());
    }

    @Test
    public void testValueOfFloat() throws Exception {
        BigDecimal value = BigDecimal.valueOf(new Random().nextInt()).movePointLeft(5);

        FloatField field = FieldFactory.valueOf(FieldType.SettlCurrFxRate.tag(), value.toPlainString().getBytes(US_ASCII));

        assertEquals("tagnum", FieldType.SettlCurrFxRate.tag(), field.getTagNum());
        assertEquals("value", value.doubleValue(), field.getValue().doubleValue(), 0.0);
        assertEquals("value", value.floatValue(), field.floatValue(), 0.0);
    }

    @Test
    public void testValueOfQty() throws Exception {
        BigDecimal value = BigDecimal.valueOf(new Random().nextInt()).movePointLeft(5);

        FloatField field = FieldFactory.valueOf(FieldType.OrderQty.tag(), value.toPlainString().getBytes(US_ASCII));

        assertEquals("tagnum", FieldType.OrderQty.tag(), field.getTagNum());
        assertEquals("value", value.doubleValue(), field.getValue().doubleValue(), 0.0);
        assertEquals("value", value.floatValue(), field.floatValue(), 0.0);
    }

    @Test
    public void testValueOfPrice() throws Exception {
        BigDecimal value = BigDecimal.valueOf(new Random().nextInt()).movePointLeft(5);

        FloatField field = FieldFactory.valueOf(FieldType.MktBidPx.tag(), value.toPlainString().getBytes(US_ASCII));

        assertEquals("tagnum", FieldType.MktBidPx.tag(), field.getTagNum());
        assertEquals("value", value.doubleValue(), field.getValue().doubleValue(), 0.0);
        assertEquals("value", value.floatValue(), field.floatValue(), 0.0);
    }

    @Test
    public void testValueOfBooleanTrue() throws Exception {
        String value = "Y";
        BooleanField field = FieldFactory.valueOf(FieldType.PossDupFlag.tag(), value.getBytes(US_ASCII));

        assertEquals("tagnum", FieldType.PossDupFlag.tag(), field.getTagNum());
        assertSame("value", Boolean.TRUE, field.getValue());
        assertTrue("value", field.booleanValue());
    }

    @Test
    public void testValueOfBooleanFalse() throws Exception {
        String value = "N";
        BooleanField field = FieldFactory.valueOf(FieldType.PossDupFlag.tag(), value.getBytes(US_ASCII));

        assertEquals("tagnum", FieldType.PossDupFlag.tag(), field.getTagNum());
        assertSame("value", Boolean.FALSE, field.getValue());
        assertFalse("value", field.booleanValue());
    }

    @Test
    public void testValueOfUtcTimestampWithMillis() throws Exception {
        String value = "19980604-08:03:31.537";
        UTCTimestampField field = FieldFactory.valueOf(FieldType.OrigTime.tag(), value.getBytes(US_ASCII));

        assertEquals("tagnum", FieldType.OrigTime.tag(), field.getTagNum());
        assertEquals("value", new LocalDate(1998, 6, 4).toDateTime(new LocalTime(8, 3, 31, 537), UTC).getMillis(), field.getValue().longValue());
    }

    @Test
    public void testValueOfUtcTimestampNoMillis() throws Exception {
        String value = "19980604-08:03:31";
        UTCTimestampField field = FieldFactory.valueOf(FieldType.OrigTime.tag(), value.getBytes(US_ASCII));

        assertEquals("tagnum", FieldType.OrigTime.tag(), field.getTagNum());
        assertEquals("value", new LocalDate(1998, 6, 4).toDateTime(new LocalTime(8, 3, 31, 0), UTC).getMillis(), field.getValue().longValue());
    }

    @Test
    public void testFromStringValueBooleanTrue() throws Exception {
        String value = "Y";
        final int tag = FieldType.PossDupFlag.tag();
        BooleanField field = FieldFactory.fromStringValue(DataType.BOOLEAN, tag, value);

        assertEquals("tagnum", tag, field.getTagNum());
        assertSame("value", Boolean.TRUE, field.getValue());
        assertTrue("value", field.booleanValue());
    }

    @Test
    public void testValueStringValueBooleanFalse() throws Exception {
        String value = "N";
        final int tag = FieldType.PossDupFlag.tag();
        BooleanField field = FieldFactory.fromStringValue(DataType.BOOLEAN, tag, value);

        assertEquals("tagnum", tag, field.getTagNum());
        assertSame("value", Boolean.FALSE, field.getValue());
        assertFalse("value", field.booleanValue());
    }
}
