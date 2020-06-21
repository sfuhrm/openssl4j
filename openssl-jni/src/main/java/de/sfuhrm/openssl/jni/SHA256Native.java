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

    protected native int nativeContextSize();
    protected native void nativeInit(ByteBuffer context);
    protected native void nativeUpdateWithByte(ByteBuffer context, byte byteData);
    protected native void nativeUpdateWithByteArray(ByteBuffer context, byte[] byteArray, int offset, int length);
    protected native void nativeUpdateWithByteBuffer(ByteBuffer context, ByteBuffer data, int offset, int length);
    protected native void nativeFinal(ByteBuffer context, byte[] digest);
}
