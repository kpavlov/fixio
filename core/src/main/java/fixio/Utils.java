package fixio;

public class Utils {

    private Utils() {
    }

    /**
     * Converts a String to ASCII byte array, provided that string contains only ASCII characters.
     *
     * @param str string to convert
     * @return a byte array
     * @see <a href="http://www.javacodegeeks.com/2010/11/java-best-practices-char-to-byte-and.html">Java Best Practices char to byte</a>
     */
    public static byte[] stringToBytesASCII(String str) {
        char[] buffer = str.toCharArray();
        final int length = buffer.length;
        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            b[i] = (byte) buffer[i];
        }
        return b;
    }

    /**
     * Converts ASCII byte array to String, provided that string contains only ASCII characters.
     *
     * @param bytes a byte array tp convert
     * @return converted String
     * @see <a href="http://www.javacodegeeks.com/2010/11/java-best-practices-char-to-byte-and.html">Java Best Practices char to byte</a>
     */
    public static String bytesToStringASCII(byte[] bytes) {
        final int length = bytes.length;
        char[] buffer = new char[length];
        for (int i = 0; i < length; i++) {
            buffer[i] = (char) bytes[i];
        }
        return new String(buffer);
    }

}
