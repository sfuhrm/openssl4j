package de.sfuhrm.openssl.jni;

/**
 * Parses digests from hexadecimal Strings.
 * @author Stephan Fuhrmann
 */
public final class Parser {

    /** Shift value for nibbles / hex digits. */
    private static final int NIBBLE_SHIFT = 4;

    /** Start of the letters in hexadecimal. */
    private static final int A_START = 10;

    /** Only singleton. */
    private Parser() {

    }

    /** Lazily initialized singleton. */
    private static Parser instance;

    /** Get a parser instance.
     * @return the single shared instance.
     * */
    public static Parser getInstance() {
        if (instance == null) {
            instance = new Parser();
        }
        return instance;
    }

    /** Parses the given hexadecimal formatted String
     * into a byte array.
     * @param in a String with hexadecimal characters having
     *           an even number of digits.
     * @return the bytes parsed from the input String.
     * @throws IllegalArgumentException if the input String has
     * an odd number of characters or non-hexadecimal
     * characters.
     * */
    public byte[] parse(final CharSequence in) {
        if ((in.length() & 0x01) != 0) {
            throw new IllegalArgumentException("Need even number of digits");
        }
        byte[] result = new byte[in.length() / 2];
        for (int i = 0; i < in.length(); i += 2) {
            int highChar = digitValue(in.charAt(i));
            int lowChar = digitValue(in.charAt(i + 1));
            result[i >> 1] = (byte) ((highChar << NIBBLE_SHIFT) | lowChar);
        }
        return result;
    }

    /** Parses a hexadecimal character to a nibble value.
     * @param d a hexadecimal character.
     * @return the parsed value.
     * @throws IllegalArgumentException if the character
     * was not inside the hex chars.
     * */
    private static int digitValue(final char d) {
        if (d >= '0' && d <= '9') {
            return d - '0';
        } else if (d >= 'a' && d <= 'f') {
            return d - 'a' + A_START;
        } else if (d >= 'A' && d <= 'F') {
            return d - 'A' + A_START;
        } else {
            throw new IllegalArgumentException(
                    "Character not parsable: " + d);
        }
    }
}
