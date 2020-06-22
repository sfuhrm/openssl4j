package de.sfuhrm.openssl.jni;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigestSpi;

/**
 * An interface to OpenSSL message digest functions.
 * @author Stephan Fuhrmann
 */
class OpenSSLMessageDigestNative extends MessageDigestSpi {

    /** Return the digest length in bytes.
     * @return the digest length in bytes.
     * */
    private static native int digestLength(ByteBuffer context);

    /** Removes a context allocated with {@linkplain #nativeContext()}.
     * @param context the context to free.
     * */
    protected static native void removeContext(ByteBuffer context);

    /** Get the list of MessageDigest algorithms supported by OpenSSL.
     * @return  an array of supported message digest algorithms from the OpenSSL library.
     * */
    protected final native static String[] listMessageDigests();

    /** Returns the context size in bytes. This is used to allocate the {@link #context direct ByteBuffer}.
     * @return a ByteBuffer containing the native message digest context.
     * */
    private final native ByteBuffer nativeContext();

    /** Initialize the context.
     * @param context the context as allocated in {@link #context}.
     * @param algorithmName the OpenSSL algorithm name as returned by {@linkplain #listMessageDigests()}.
     * */
    private final native void nativeInit(ByteBuffer context, String algorithmName);

    /** Update the context with a single byte.
     * @param context the context as allocated in {@link #context}.
     * @param byteData the byte to update the context with.
     * */
    private final native void nativeUpdateWithByte(ByteBuffer context, byte byteData);

    /** Update the context with an array.
     * @param context the context as allocated in {@link #context}.
     * @param byteArray the array to update the context with.
     * @param offset the start offset of the array data to update the context with.
     * @param length the number of bytes to update the context with.
     * */
    private final native void nativeUpdateWithByteArray(ByteBuffer context, byte[] byteArray, int offset, int length);

    /** Update the context with a direct byte buffer.
     * @param context the context as allocated in {@link #context}.
     * @param data the byte buffer to update the context with.
     * @param offset the start offset of the buffer data to update the context with.
     * @param length the number of bytes to update the context with.
     * */
    private final native void nativeUpdateWithByteBuffer(ByteBuffer context, ByteBuffer data, int offset, int length);

    /** Do the final digest calculation and return it.
     * @param context the context as allocated in {@link #context}.
     * @param digest the target array to write the digest data to.
     * */
    private final native void nativeFinal(ByteBuffer context, byte[] digest);

    /** A MD5 context where the state of the current calculation is stored.  */
    private final ByteBuffer context;

    /** The OpenSSL algorithm name as returned by {@linkplain #listMessageDigests()}. */
    private final String algorithmName;

    OpenSSLMessageDigestNative(String openSslName) {
        try {
            NativeLoader.loadAll();
            algorithmName = openSslName;
            context = nativeContext();
            PhantomReferenceCleanup.enqueueForCleanup(this);
            engineReset();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Get the native context that needs to be cleared at GC. */
    final ByteBuffer getContext() {
        return context;
    }

    @Override
    protected int engineGetDigestLength() {
        return digestLength(context);
    }

    @Override
    protected final void engineUpdate(ByteBuffer input) {
        if (!input.hasRemaining()) {
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
    protected final void engineUpdate(byte inputByte) {
        nativeUpdateWithByte(context, inputByte);
    }

    @Override
    protected final void engineUpdate(byte[] input, int offset, int len) {
        nativeUpdateWithByteArray(context, input, offset, len);
    }

    @Override
    protected final byte[] engineDigest() {
        byte[] result = new byte[digestLength(context)];
        nativeFinal(context, result);
        return result;
    }

    @Override
    protected final void engineReset() {
        nativeInit(context, algorithmName);
    }
}
