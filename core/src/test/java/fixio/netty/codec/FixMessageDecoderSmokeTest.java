package fixio.netty.codec;

import fixio.fixprotocol.FixMessageImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static fixio.netty.codec.DecodingTestHelper.decodeOne;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class FixMessageDecoderSmokeTest {

    private static FixMessageDecoder decoder;

    public FixMessageDecoderSmokeTest(String message) {
        this.message = message;
    }

    @Parameterized.Parameters(name = "msg: {0}")
    public static Object[] data() throws IOException, URISyntaxException {
        Path path = Paths.get(Objects.requireNonNull(FixMessageDecoderSmokeTest.class.getClassLoader()
                .getResource("example-messages.txt")).toURI());

        return Files.lines(path)
                .filter(l -> l.startsWith("8="))
                .toArray(String[]::new);
    }

    private final String message;

    @BeforeClass
    public static void setUp() {
        decoder = new FixMessageDecoder();
    }

    @Test
    public void shouldDecode() {
        FixMessageImpl result = decodeOne(message, decoder);
        assertThat(result).isNotNull();
    }

}
