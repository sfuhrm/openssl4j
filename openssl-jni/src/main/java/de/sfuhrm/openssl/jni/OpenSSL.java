package de.sfuhrm.openssl.jni;

/** Class definitions for the message digest spis.
 * @author Stephan Fuhrmann
 *  */
public final class OpenSSL {

    private OpenSSL() {
        // no instances allowed
    }

    public final static class MD5 extends OpenSSLMessageDigestNative {
        public MD5() { super("MD5"); }
    }

    public final static class SHA1 extends OpenSSLMessageDigestNative {
        public SHA1() { super("SHA1"); }
    }

    public final static class SHA_224 extends OpenSSLMessageDigestNative {
        public SHA_224() { super("SHA224"); }
    }

    public final static class SHA_256 extends OpenSSLMessageDigestNative {
        public SHA_256() { super("SHA256"); }
    }

    public final static class SHA_384 extends OpenSSLMessageDigestNative {
        public SHA_384() { super("SHA384"); }
    }

    public final static class SHA_512 extends OpenSSLMessageDigestNative {
        public SHA_512() { super("SHA512"); }
    }

    public final static class SHA_512_224 extends OpenSSLMessageDigestNative {
        public SHA_512_224() { super("SHA512-224"); }
    }

    public final static class SHA_512_256 extends OpenSSLMessageDigestNative {
        public SHA_512_256() { super("SHA512-256"); }
    }

    public final static class SHA3_224 extends OpenSSLMessageDigestNative {
        public SHA3_224() { super("SHA3-224"); }
    }

    public final static class SHA3_256 extends OpenSSLMessageDigestNative {
        public SHA3_256() { super("SHA3-256"); }
    }

    public final static class SHA3_384 extends OpenSSLMessageDigestNative {
        public SHA3_384() { super("SHA3-384"); }
    }

    public final static class SHA3_512 extends OpenSSLMessageDigestNative {
        public SHA3_512() { super("SHA3-512"); }
    }

    public final static class BLAKE2b512 extends OpenSSLMessageDigestNative {
        public BLAKE2b512() { super("BLAKE2b512"); }
    }

    public final static class BLAKE2s256 extends OpenSSLMessageDigestNative {
        public BLAKE2s256() { super("BLAKE2s256"); }
    }

    public final static class MD4 extends OpenSSLMessageDigestNative {
        public MD4() { super("MD4"); }
    }

    public final static class RIPEMD160 extends OpenSSLMessageDigestNative {
        public RIPEMD160() { super("RIPEMD160"); }
    }

    public final static class SM3 extends OpenSSLMessageDigestNative {
        public SM3() { super("SM3"); }
    }

    public final static class Whirlpool extends OpenSSLMessageDigestNative {
        public Whirlpool() { super("whirlpool"); }
    }
}
