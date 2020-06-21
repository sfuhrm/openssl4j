package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * SHA3-512 message digest adapter to the OpenSSL SHA3-512 functions.
 * @author Stephan Fuhrmann
 */
public class SHA3_512Native extends AbstractNative {

    @Override
    protected int digestLength() {
        return 64;
    }

    protected native void nativeInit(ByteBuffer context);
}
