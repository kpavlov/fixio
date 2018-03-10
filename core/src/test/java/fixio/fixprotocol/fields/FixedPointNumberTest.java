package fixio.fixprotocol.fields;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FixedPointNumberTest {

    @Test
    public void testZeroToString() {
        final FixedPointNumber fixedPointNumber = new FixedPointNumber(0, 0);
        assertEquals("0", fixedPointNumber.toString());
    }

    @Test
    public void testScaledZeroToString() {
        final FixedPointNumber fixedPointNumber = new FixedPointNumber(0, 20);
        assertEquals("0", fixedPointNumber.toString());
    }
}