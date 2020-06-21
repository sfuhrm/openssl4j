package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * SHA3-224 message digest adapter to the OpenSSL SHA3-224 functions.
 * @author Stephan Fuhrmann
 */
public class SHA3_224Native extends AbstractNative {
    protected native void nativeInit(ByteBuffer context);
}
