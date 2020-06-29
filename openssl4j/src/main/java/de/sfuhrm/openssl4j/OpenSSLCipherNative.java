package de.sfuhrm.openssl4j;

import javax.crypto.CipherSpi;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigestSpi;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An interface to OpenSSL cipher functions.
 * @author Stephan Fuhrmann
 */
class OpenSSLCipherNative /* extends CipherSpi */ {

    /** Get the list of Cipher algorithms supported by OpenSSL.
     * @return  an array of supported cipher algorithms from the OpenSSL library.
     * */
    private native static String[] listCiphers();

    /** Returns the context size in bytes. This is used to allocate the {@link #context direct ByteBuffer}.
     * @return a ByteBuffer containing the native message digest context.
     * */
    private final native ByteBuffer nativeContext();

    /** Removes a context allocated with {@linkplain #nativeContext()}.
     * @param context the context to free.
     * */
    private static native void removeContext(ByteBuffer context);

    /** Get the list of digest algorithms supported by the OpenSSL library.
     * @return a Set of supported message digest algorithms.
     *  */
    protected static Set<String> getCipherList() {
        String[] messageDigestAlgorithms = listCiphers();
        return new HashSet<>(Arrays.asList(messageDigestAlgorithms));
    }

    /** A native message digest context where the state of the current calculation is stored.
     * Allocated with {@linkplain #nativeContext()}, freed by the
     * {@linkplain PhantomReferenceCleanup} with {@linkplain #free(ByteBuffer)}.
     * */
    private final ByteBuffer context;

    /** The OpenSSL algorithm name as returned by {@linkplain #listCiphers()}. */
    private final String algorithmName;

    OpenSSLCipherNative(String openSslName) {
        try {
            NativeLoader.loadAll();
            algorithmName = Objects.requireNonNull(openSslName);
            context = nativeContext();
            PhantomReferenceCleanup.enqueueForCleanup(this, OpenSSLCipherNative::free, context);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Free the native context that came from {@linkplain #nativeContext()}.
     * @param context the context allocated with {@linkplain #nativeContext()}.
     * */
    protected static void free(ByteBuffer context) {
        Objects.requireNonNull(context);
        if (! context.isDirect()) {
            throw new IllegalStateException("Illegal buffer passed in");
        }
        removeContext(context);
    }
}
