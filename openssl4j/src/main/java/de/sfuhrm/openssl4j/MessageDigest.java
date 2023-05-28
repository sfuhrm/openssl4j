package de.sfuhrm.openssl4j;

/** Class definitions for the message digest spis.
 * @author Stephan Fuhrmann
 *  */
public final class MessageDigest {

    private MessageDigest() {
        // no instances allowed
    }

    /** MD5 message digest implementation.
     * */
    public final static class MD5 extends OpenSSLMessageDigestNative {

        /** Creates a new instance. */
        public MD5() { super("MD5"); }
    }

    /** SHA1 message digest implementation.
     * */
    public final static class SHA1 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public SHA1() { super("SHA1"); }
    }

    /** SHA-224 message digest implementation.
     * */
    public final static class SHA_224 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public SHA_224() { super("SHA224"); }
    }

    /** SHA-256 message digest implementation.
     * */
    public final static class SHA_256 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public SHA_256() { super("SHA256"); }
    }

    /** SHA-384 message digest implementation.
     * */
    public final static class SHA_384 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public SHA_384() { super("SHA384"); }
    }

    /** SHA-512 message digest implementation.
     * */
    public final static class SHA_512 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public SHA_512() { super("SHA512"); }
    }

    /** SHA-512/224 message digest implementation.
     * */
    public final static class SHA_512_224 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public SHA_512_224() { super("SHA512-224"); }
    }

    /** SHA-512/256 message digest implementation.
     * */
    public final static class SHA_512_256 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public SHA_512_256() { super("SHA512-256"); }
    }

    /** SHA3-224 message digest implementation.
     * */
    public final static class SHA3_224 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public SHA3_224() { super("SHA3-224"); }
    }

    /** SHA3-256 message digest implementation.
     * */
    public final static class SHA3_256 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public SHA3_256() { super("SHA3-256"); }
    }

    /** SHA3-384 message digest implementation.
     * */
    public final static class SHA3_384 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public SHA3_384() { super("SHA3-384"); }
    }

    /** SHA3-512 message digest implementation.
     * */
    public final static class SHA3_512 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public SHA3_512() { super("SHA3-512"); }
    }

    /** BLAKE2b512 message digest implementation.
     * */
    public final static class BLAKE2b512 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public BLAKE2b512() { super("BLAKE2b512"); }
    }

    /** BLAKE2s256 message digest implementation.
     * */
    public final static class BLAKE2s256 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public BLAKE2s256() { super("BLAKE2s256"); }
    }

    /** MD4 message digest implementation.
     * */
    public final static class MD4 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public MD4() { super("MD4"); }
    }

    /** RIPEMD160 message digest implementation.
     * */
    public final static class RIPEMD160 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public RIPEMD160() { super("RIPEMD160"); }
    }

    /** SM3 message digest implementation.
     * */
    public final static class SM3 extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public SM3() { super("SM3"); }
    }

    /** Whirlpool message digest implementation.
     * */
    public final static class Whirlpool extends OpenSSLMessageDigestNative {
        /** Creates a new instance. */
        public Whirlpool() { super("whirlpool"); }
    }
}
