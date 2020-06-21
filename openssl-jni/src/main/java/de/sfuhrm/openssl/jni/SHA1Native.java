package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * SHA1 message digest adapter to the OpenSSL SHA1 functions.
 * @author Stephan Fuhrmann
 */
public class SHA1Native extends AbstractNative {

    @Override
    protected int digestLength() {
        return 20;
    }

    protected native int nativeContextSize();
    protected native void nativeInit(ByteBuffer context);
    protected native void nativeUpdateWithByte(ByteBuffer context, byte byteData);
    protected native void nativeUpdateWithByteArray(ByteBuffer context, byte[] byteArray, int offset, int length);
    protected native void nativeUpdateWithByteBuffer(ByteBuffer context, ByteBuffer data, int offset, int length);
    protected native void nativeFinal(ByteBuffer context, byte[] digest);
}
