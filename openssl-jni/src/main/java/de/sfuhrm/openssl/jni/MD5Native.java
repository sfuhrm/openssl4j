package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * MD5 message digest adapter to the OpenSSL MD5 functions.
 * @author Stephan Fuhrmann
 */
public class MD5Native extends AbstractNative {
    protected native void nativeInit(ByteBuffer context);
}
