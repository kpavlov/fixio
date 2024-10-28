package fixio.fixprotocol.fields;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixedPointNumberTest {

    @Test
    void zeroToString() {
        final FixedPointNumber fixedPointNumber = new FixedPointNumber(0, 0);
        assertEquals("0", fixedPointNumber.toString());
    }

    @Test
    void scaledZeroToString() {
        final FixedPointNumber fixedPointNumber = new FixedPointNumber(0, 20);
        assertEquals("0", fixedPointNumber.toString());
    }
}