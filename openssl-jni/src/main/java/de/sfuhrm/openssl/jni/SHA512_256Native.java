package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * SHA-512-256 message digest adapter to the OpenSSL SHA-512-256 functions.
 * @author Stephan Fuhrmann
 */
public class SHA512_256Native extends AbstractNative {
    protected native void nativeInit(ByteBuffer context);
}
