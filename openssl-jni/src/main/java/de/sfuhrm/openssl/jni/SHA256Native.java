package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * SHA-256 message digest adapter to the OpenSSL SHA-256 functions.
 * @author Stephan Fuhrmann
 */
public class SHA256Native extends AbstractNative {

    @Override
    protected int digestLength() {
        return 32;
    }

    protected native void nativeInit(ByteBuffer context);
}
