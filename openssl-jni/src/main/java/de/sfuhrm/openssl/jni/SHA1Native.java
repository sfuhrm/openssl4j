package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * SHA1 message digest adapter to the OpenSSL SHA1 functions.
 * @author Stephan Fuhrmann
 */
public class SHA1Native extends AbstractNative {
    protected native void nativeInit(ByteBuffer context);
}
