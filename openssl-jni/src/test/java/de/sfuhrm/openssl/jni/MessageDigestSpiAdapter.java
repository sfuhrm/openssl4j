package de.sfuhrm.openssl.jni;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.MessageDigestSpi;

/**
 * Adapter that maps calls to protected methods in a {@link MessageDigestSpi} instance.
 * @author Stephan Fuhrmann
 */
public final class MessageDigestSpiAdapter {

    private final MessageDigestSpi messageDigestSpi;

    private final Method engineUpdateByte;
    private final Method engineUpdateByteArrayIntInt;
    private final Method engineUpdateByteBuffer;
    private final Method engineDigest;
    private final Method engineReset;

    public MessageDigestSpiAdapter(MessageDigestSpi inner) throws NoSuchMethodException {
        messageDigestSpi = inner;
        engineUpdateByte = inner.getClass().getDeclaredMethod("engineUpdate", byte.class);
        engineUpdateByteArrayIntInt = inner.getClass().getDeclaredMethod("engineUpdate", byte[].class, int.class, int.class);
        engineUpdateByteBuffer = inner.getClass().getDeclaredMethod("engineUpdate", ByteBuffer.class);
        engineDigest = inner.getClass().getDeclaredMethod("engineDigest");
        engineReset = inner.getClass().getDeclaredMethod("engineReset");
    }

    public void engineUpdate(byte input) throws InvocationTargetException, IllegalAccessException {
        engineUpdateByte.invoke(messageDigestSpi, input);
    }

    public void engineUpdate(byte[] input, int offset, int len) throws InvocationTargetException, IllegalAccessException {
        engineUpdateByteArrayIntInt.invoke(messageDigestSpi, input, offset, len);
    }

    public void engineUpdate(ByteBuffer byteBuffer) throws InvocationTargetException, IllegalAccessException {
        engineUpdateByteBuffer.invoke(messageDigestSpi, byteBuffer);
    }

    public byte[] engineDigest() throws InvocationTargetException, IllegalAccessException {
        return (byte[]) engineDigest.invoke(messageDigestSpi);
    }

    public void engineReset() throws InvocationTargetException, IllegalAccessException {
        engineReset.invoke(messageDigestSpi);
    }
}
