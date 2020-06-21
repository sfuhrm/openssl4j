package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * SHA-512-224 message digest adapter to the OpenSSL SHA-512-224 functions.
 * @author Stephan Fuhrmann
 */
public class SHA512_224Native extends AbstractNative {
    protected native void nativeInit(ByteBuffer context);
}
