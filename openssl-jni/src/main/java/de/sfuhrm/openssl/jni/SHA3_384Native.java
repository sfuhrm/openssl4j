package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * SHA3-384 message digest adapter to the OpenSSL SHA3-384 functions.
 * @author Stephan Fuhrmann
 */
public class SHA3_384Native extends OpenSSLMessageDigestNative {
    public SHA3_384Native() {
        super("SHA3-384");
    }
}
