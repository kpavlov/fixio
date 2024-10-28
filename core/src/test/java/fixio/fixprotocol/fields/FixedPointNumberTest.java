package fixio.fixprotocol.fields;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FixedPointNumberTest {

    @Test
    void zeroToString() {
        final FixedPointNumber fixedPointNumber = new FixedPointNumber(0, 0);
        assertThat(fixedPointNumber.toString()).isEqualTo("0");
    }

    @Test
    void scaledZeroToString() {
        final FixedPointNumber fixedPointNumber = new FixedPointNumber(0, 20);
        assertThat(fixedPointNumber.toString()).isEqualTo("0");
    }
}