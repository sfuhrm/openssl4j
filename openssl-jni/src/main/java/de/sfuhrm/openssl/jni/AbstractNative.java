package de.sfuhrm.openssl.jni;

import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.nio.ByteBuffer;
import java.security.MessageDigestSpi;

/**
 * MD5 message digest adapter to the OpenSSL MD5 functions.
 * @author Stephan Fuhrmann
 */
abstract class AbstractNative extends MessageDigestSpi {

    /** Return the digest length in bytes.
     * @return the digest length in bytes.
     * */
    protected abstract int digestLength();

    /** Removes a context allocated with {@linkplain #nativeContext()}. */
    static native void removeContext(ByteBuffer context);

    /** Returns the context size in bytes. This is used to allocate the {@link #context direct ByteBuffer}.
     * @return a ByteBuffer containing the native message digest context.
     * */
    protected native ByteBuffer nativeContext();

    /** Initialize the context.
     * @param context the context as allocated in {@link #context}.
     * */
    protected abstract void nativeInit(ByteBuffer context);

    /** Update the context with a single byte.
     * @param context the context as allocated in {@link #context}.
     * @param byteData the byte to update the context with.
     * */
    protected native void nativeUpdateWithByte(ByteBuffer context, byte byteData);

    /** Update the context with an array.
     * @param context the context as allocated in {@link #context}.
     * @param byteArray the array to update the context with.
     * @param offset the start offset of the array data to update the context with.
     * @param length the number of bytes to update the context with.
     * */
    protected native void nativeUpdateWithByteArray(ByteBuffer context, byte[] byteArray, int offset, int length);

    /** Update the context with a direct byte buffer.
     * @param context the context as allocated in {@link #context}.
     * @param data the byte buffer to update the context with.
     * @param offset the start offset of the buffer data to update the context with.
     * @param length the number of bytes to update the context with.
     * */
    protected native void nativeUpdateWithByteBuffer(ByteBuffer context, ByteBuffer data, int offset, int length);

    /** Do the final digest calculation and return it.
     * @param context the context as allocated in {@link #context}.
     * @param digest the target array to write the digest data to.
     * */
    protected native void nativeFinal(ByteBuffer context, byte[] digest);

    /** A MD5 context where the state of the current calculation is stored.  */
    private final ByteBuffer context;

    private static final ReferenceQueue<PhantomReference<ByteBuffer>> BYTE_BUFFER_REFERENCE_QUEUE = new ReferenceQueue<>();

    public AbstractNative() {
        try {
            NativeLoader.loadAll();
            context = nativeContext();
            PhantomReferenceCleanup.enqueueForCleanup(context);
            engineReset();
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
        byte[] result = new byte[digestLength()];
        nativeFinal(context, result);
        return result;
    }

    @Override
    protected void engineReset() {
        nativeInit(context);
    }
}
