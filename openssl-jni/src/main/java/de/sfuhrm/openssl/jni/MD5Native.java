package de.sfuhrm.openssl.jni;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.security.MessageDigestSpi;

/**
 * MD5 message digest adapter to the OpenSSL MD5 functions.
 * @author Stephan Fuhrmann
 */
public class MD5Native extends MessageDigestSpi {

    private static final int DIGEST_LENGTH = 16;

    /** Returns the context size in bytes. This is used to allocate the {@link #context direct ByteBuffer}. */
    private static native int nativeContextSize();

    /** Initialize the context.
     * @param context the context as allocated in {@link #context}.
     * */
    private static native void nativeInit(ByteBuffer context);

    /** Update the context with a single byte.
     * @param context the context as allocated in {@link #context}.
     * @param byteData the byte to update the context with.
     * */
    private static native void nativeUpdateWithByte(ByteBuffer context, byte byteData);

    /** Update the context with an array.
     * @param context the context as allocated in {@link #context}.
     * @param byteArray the array to update the context with.
     * @param offset the start offset of the array data to update the context with.
     * @param length the number of bytes to update the context with.
     * */
    private static native void nativeUpdateWithByteArray(ByteBuffer context, byte[] byteArray, int offset, int length);

    /** Update the context with a direct byte buffer.
     * @param context the context as allocated in {@link #context}.
     * @param data the byte buffer to update the context with.
     * @param offset the start offset of the buffer data to update the context with.
     * @param length the number of bytes to update the context with.
     * */
    private static native void nativeUpdateWithByteBuffer(ByteBuffer context, ByteBuffer data, int offset, int length);

    /** Do the final digest calculation and return it.
     * @param context the context as allocated in {@link #context}.
     * @param digest the target array to write the digest data to.
     * */
    private static native void nativeFinal(ByteBuffer context, byte[] digest);

    /** A MD5 context where the state of the current calculation is stored.  */
    private final ByteBuffer context;

    public MD5Native() {
        context = ByteBuffer.allocateDirect(nativeContextSize());
        try {
            NativeLoader.loadAll();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void engineUpdate(ByteBuffer input) {
        if (input.hasRemaining() == false) {
            return;
        }
        int remaining = input.remaining();
        int offset = input.position();
        if (input.isDirect()) {
            nativeUpdateWithByteBuffer(context, input, offset, remaining);
        } else {
            byte[] array = input.array();
            nativeUpdateWithByteArray(context, array, 0, array.length);
        }
        input.position(remaining);
    }

    @Override
    protected void engineUpdate(byte inputByte) {
        nativeUpdateWithByte(context, inputByte);
    }

    @Override
    protected void engineUpdate(byte[] input, int offset, int len) {
        nativeUpdateWithByteArray(context, input, offset, len);
    }

    @Override
    protected byte[] engineDigest() {
        byte[] result = new byte[DIGEST_LENGTH];
        nativeFinal(context, result);
        return result;
    }

    @Override
    protected void engineReset() {
        nativeInit(context);
    }
}
