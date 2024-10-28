package fixio.netty.codec;

import fixio.fixprotocol.FixMessageImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static fixio.netty.codec.DecodingTestHelper.decodeOne;
import static org.assertj.core.api.Assertions.assertThat;

public class FixMessageDecoderSmokeTest {

    private static FixMessageDecoder decoder;
    private String message;

    public void initFixMessageDecoderSmokeTest(String message) {
        this.message = message;
    }

    public static Object[] data() throws IOException, URISyntaxException {
        Path path = Path.of(Objects.requireNonNull(FixMessageDecoderSmokeTest.class.getClassLoader()
                .getResource("example-messages.txt")).toURI());

        return Files.lines(path)
                .filter(l -> l.startsWith("8="))
                .toArray(String[]::new);
    }

    @BeforeAll
    static void setUp() {
        decoder = new FixMessageDecoder();
    }

    @MethodSource("data")
    @ParameterizedTest(name = "msg: {0}")
    void shouldDecode(String message) {
        initFixMessageDecoderSmokeTest(message);
        FixMessageImpl result = decodeOne(message, decoder);
        assertThat(result).isNotNull();
    }

}
