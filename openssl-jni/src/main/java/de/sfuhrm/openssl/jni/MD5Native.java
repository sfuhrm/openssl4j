package de.sfuhrm.openssl.jni;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.security.MessageDigestSpi;

/**
 * MD5 message digest adapter to the OpenSSL MD5 functions.
 * @author Stephan Fuhrmann
 */
public class MD5Native extends AbstractNative {

    @Override
    protected int digestLength() {
        return 16;
    }

    protected native void nativeInit(ByteBuffer context);
}
