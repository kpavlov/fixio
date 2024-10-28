package fixio.netty.codec;

import fixio.fixprotocol.FixMessageImpl;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DecodingTestHelper {

    private static String normalizeMessage(String source) {
        return source.replace('|', '\u0001');
    }

    public static List<Object> decode(String message, FixMessageDecoder decoder) {
        String[] tags = normalizeMessage(message).split("\u0001");

        List<Object> result = new ArrayList<>();
        for (String tag : tags) {
            decoder.decode(null, Unpooled.wrappedBuffer(tag.getBytes(StandardCharsets.US_ASCII)), result);
        }
        return result;
    }

    public static FixMessageImpl decodeOne(String message, FixMessageDecoder decoder) {
        String[] tags = normalizeMessage(message).split("\u0001");

        List<Object> result = new ArrayList<>();
        for (String tag : tags) {
            decoder.decode(null, Unpooled.wrappedBuffer(tag.getBytes(StandardCharsets.US_ASCII)), result);
        }
        return (FixMessageImpl) result.get(0);
    }
}
