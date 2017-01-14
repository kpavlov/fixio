package fixio;

public class Utils {

    private Utils() {
    }


    /**
     * @link http://www.javacodegeeks.com/2010/11/java-best-practices-char-to-byte-and.html
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
     * @link http://www.javacodegeeks.com/2010/11/java-best-practices-char-to-byte-and.html
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
