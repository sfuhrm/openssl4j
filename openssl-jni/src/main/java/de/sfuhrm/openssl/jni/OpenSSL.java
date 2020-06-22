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

    public final static class SHA224 extends OpenSSLMessageDigestNative {
        public SHA224() { super("SHA224"); }
    }

    public final static class SHA256 extends OpenSSLMessageDigestNative {
        public SHA256() { super("SHA256"); }
    }

    public final static class SHA384 extends OpenSSLMessageDigestNative {
        public SHA384() { super("SHA384"); }
    }

    public final static class SHA512 extends OpenSSLMessageDigestNative {
        public SHA512() { super("SHA512"); }
    }

    public final static class SHA512_224 extends OpenSSLMessageDigestNative {
        public SHA512_224() { super("SHA512-224"); }
    }

    public final static class SHA512_256 extends OpenSSLMessageDigestNative {
        public SHA512_256() { super("SHA512-256"); }
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
}
