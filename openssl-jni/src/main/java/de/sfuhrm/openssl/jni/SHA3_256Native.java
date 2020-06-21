package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * SHA3-256 message digest adapter to the OpenSSL SHA3-256 functions.
 * @author Stephan Fuhrmann
 */
public class SHA3_256Native extends AbstractNative {
    protected native void nativeInit(ByteBuffer context);
}
