package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * SHA-256 message digest adapter to the OpenSSL SHA-256 functions.
 * @author Stephan Fuhrmann
 */
public class SHA256Native extends OpenSSLMessageDigestNative {
    public SHA256Native() {
        super("SHA256");
    }
}
