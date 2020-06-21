package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * SHA3-384 message digest adapter to the OpenSSL SHA3-384 functions.
 * @author Stephan Fuhrmann
 */
public class SHA3_384Native extends AbstractNative {
    protected native void nativeInit(ByteBuffer context);
}
