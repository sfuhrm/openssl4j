package de.sfuhrm.openssl4j;

/**
 * Formats digests to hexadecimal Strings.
 *
 * @author Stephan Fuhrmann
 */
public final class Formatter {

  /** Only singleton. */
  private Formatter() {}

  /** Lazily initialized singleton. */
  private static Formatter instance;

  /**
   * Get a formatter instance.
   *
   * @return the single shared instance.
   */
  public static Formatter getInstance() {
    if (instance == null) {
      instance = new Formatter();
    }
    return instance;
  }

  /**
   * Formats the given digest bytes to a hexadecimal String.
   *
   * @param digest the digest bytes to format.
   * @return the digest bytes formatted as hexadecimal String. Every byte is formatted as two
   *     characters.
   */
  public String format(final byte[] digest) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b : digest) {
      stringBuilder.append(digitValue(0x0f & (b >> 4))).append(digitValue(0x0f & b));
    }
    return stringBuilder.toString();
  }

  /**
   * Formats a nibble to a char.
   *
   * @param b a nibble value between 0 and 15 (inclusive).
   * @return the character value.
   * @throws IllegalArgumentException if the character was not inside the hex chars.
   */
  private static char digitValue(final int b) {
    if (b >= 0 && b <= 9) {
      return (char) (b + '0');
    } else if (b >= 0xa && b <= 0xf) {
      return (char) (b - 10 + 'a');
    } else {
      throw new IllegalArgumentException("Digit not formattable: " + b);
    }
  }
}
