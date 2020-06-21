package de.sfuhrm.openssl.jni;

import java.nio.ByteBuffer;

/**
 * SHA-224 message digest adapter to the OpenSSL SHA-224 functions.
 * @author Stephan Fuhrmann
 */
public class SHA224Native extends AbstractNative {

    @Override
    protected int digestLength() {
        return 28;
    }

    protected native int nativeContextSize();
    protected native void nativeInit(ByteBuffer context);
    protected native void nativeUpdateWithByte(ByteBuffer context, byte byteData);
    protected native void nativeUpdateWithByteArray(ByteBuffer context, byte[] byteArray, int offset, int length);
    protected native void nativeUpdateWithByteBuffer(ByteBuffer context, ByteBuffer data, int offset, int length);
    protected native void nativeFinal(ByteBuffer context, byte[] digest);
}
