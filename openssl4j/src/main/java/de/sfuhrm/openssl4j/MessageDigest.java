package de.sfuhrm.openssl4j;

/**
 * Class definitions for the message digest spis.
 *
 * @author Stephan Fuhrmann
 */
public final class MessageDigest {

  private MessageDigest() {
    // No instances allowed.
  }

  /** BLAKE2b512 message digest implementation. */
  public static final class BLAKE2b512 extends OpenSSLMessageDigestNative {
    public BLAKE2b512() {
      super("BLAKE2b512");
    }
  }

  /** BLAKE2s256 message digest implementation. */
  public static final class BLAKE2s256 extends OpenSSLMessageDigestNative {
    public BLAKE2s256() {
      super("BLAKE2s256");
    }
  }

  /** MD4 message digest implementation. */
  public static final class MD4 extends OpenSSLMessageDigestNative {
    public MD4() {
      super("MD4");
    }
  }

  /** MD5 message digest implementation. */
  public static final class MD5 extends OpenSSLMessageDigestNative {
    public MD5() {
      super("MD5");
    }
  }

  /** RIPEMD160 message digest implementation. */
  public static final class RIPEMD160 extends OpenSSLMessageDigestNative {
    public RIPEMD160() {
      super("RIPEMD160");
    }
  }

  /** SHA1 message digest implementation. */
  public static final class SHA1 extends OpenSSLMessageDigestNative {
    public SHA1() {
      super("SHA1");
    }
  }

  /** SHA-224 message digest implementation. */
  public static final class SHA_224 extends OpenSSLMessageDigestNative {
    public SHA_224() {
      super("SHA224");
    }
  }

  /** SHA-256 message digest implementation. */
  public static final class SHA_256 extends OpenSSLMessageDigestNative {
    public SHA_256() {
      super("SHA256");
    }
  }

  /** SHA3-224 message digest implementation. */
  public static final class SHA3_224 extends OpenSSLMessageDigestNative {
    public SHA3_224() {
      super("SHA3-224");
    }
  }

  /** SHA3-256 message digest implementation. */
  public static final class SHA3_256 extends OpenSSLMessageDigestNative {
    public SHA3_256() {
      super("SHA3-256");
    }
  }

  /** SHA3-384 message digest implementation. */
  public static final class SHA3_384 extends OpenSSLMessageDigestNative {
    public SHA3_384() {
      super("SHA3-384");
    }
  }

  /** SHA3-512 message digest implementation. */
  public static final class SHA3_512 extends OpenSSLMessageDigestNative {
    public SHA3_512() {
      super("SHA3-512");
    }
  }

  /** SHA-384 message digest implementation. */
  public static final class SHA_384 extends OpenSSLMessageDigestNative {
    public SHA_384() {
      super("SHA384");
    }
  }

  /** SHA-512 message digest implementation. */
  public static final class SHA_512 extends OpenSSLMessageDigestNative {
    public SHA_512() {
      super("SHA512");
    }
  }

  /** SHA-512/224 message digest implementation. */
  public static final class SHA_512_224 extends OpenSSLMessageDigestNative {
    public SHA_512_224() {
      super("SHA512-224");
    }
  }

  /** SHA-512/256 message digest implementation. */
  public static final class SHA_512_256 extends OpenSSLMessageDigestNative {
    public SHA_512_256() {
      super("SHA512-256");
    }
  }

  /** SM3 message digest implementation. */
  public static final class SM3 extends OpenSSLMessageDigestNative {
    public SM3() {
      super("SM3");
    }
  }

  /** Whirlpool message digest implementation. */
  public static final class Whirlpool extends OpenSSLMessageDigestNative {
    public Whirlpool() {
      super("whirlpool");
    }
  }
}
