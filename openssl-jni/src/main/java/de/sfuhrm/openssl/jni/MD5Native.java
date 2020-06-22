package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * MD5 message digest adapter to the OpenSSL MD5 functions.
 * @author Stephan Fuhrmann
 */
public final class MD5Native extends OpenSSLMessageDigestNative {
    public MD5Native() {
        super("MD5");
    }
}
