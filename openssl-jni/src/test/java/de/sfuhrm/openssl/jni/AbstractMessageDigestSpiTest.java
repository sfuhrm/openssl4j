package de.sfuhrm.openssl.jni;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.MessageDigestSpi;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Abstract test base for a {@linkplain MessageDigestSpi} implementation.
 * @author Stephan Fuhrmann
 */
public abstract class AbstractMessageDigestSpiTest extends BasicTest {

    @Test
    public void digestWithNoData() throws Exception {
        MessageDigest mdSpi = newTestMD();
        byte[] actualDigest = mdSpi.digest();

        MessageDigest reference = newReferenceMD();
        byte[] expectedDigest = reference.digest();

        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    @Test
    public void digestWithReset() throws Exception {
        MessageDigest mdSpi = newTestMD();

        byte[] data = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);

        byte[] actualDigest = mdSpi.digest(data);

        MessageDigest reference = newReferenceMD();
        byte[] expectedDigest = reference.digest(data);

        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    @Test
    public void updateWithFullArray() throws Exception {

        byte[] data = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);

        MessageDigest mdSpi = newTestMD();
        mdSpi.update(data);
        byte[] actualDigest = mdSpi.digest();

        MessageDigest reference = newReferenceMD();
        byte[] expectedDigest = reference.digest(data);

        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    @Test
    public void updateWithSingleBytes() throws Exception {

        byte[] data = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);

        MessageDigest mdSpi = newTestMD();
        for (byte val : data) {
            mdSpi.update(val);
        }
        byte[] actualDigest = mdSpi.digest();

        MessageDigest reference = newReferenceMD();
        for (byte val : data) {
            reference.update(val);
        }
        byte[] expectedDigest = reference.digest();
        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    @Test
    public void updateWithHeapByteBuffer() throws Exception {

        byte[] data = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);
        ByteBuffer bb = ByteBuffer.wrap(data);

        ByteBuffer actualCopy = bb.duplicate();
        MessageDigest mdSpi = newTestMD();
        mdSpi.update(actualCopy);
        byte[] actualDigest = mdSpi.digest();

        MessageDigest reference = newReferenceMD();
        ByteBuffer expectedCopy = bb.duplicate();
        reference.update(expectedCopy);

        byte[] expectedDigest = reference.digest();
        assertEquals(expectedCopy.position(), actualCopy.position());
        assertEquals(expectedCopy.limit(), actualCopy.limit());
        assertEquals(expectedCopy.capacity(), actualCopy.capacity());
        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    @Test
    public void updateWithDirectByteBuffer() throws Exception {

        byte[] data = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length);
        bb.put(data);
        bb.flip();

        ByteBuffer actualCopy = bb.duplicate();
        MessageDigest mdSpi = newTestMD();
        mdSpi.update(actualCopy);
        byte[] actualDigest = mdSpi.digest();

        MessageDigest reference = newReferenceMD();
        ByteBuffer expectedCopy = bb.duplicate();
        reference.update(expectedCopy);

        byte[] expectedDigest = reference.digest();
        assertEquals(expectedCopy.position(), actualCopy.position());
        assertEquals(expectedCopy.limit(), actualCopy.limit());
        assertEquals(expectedCopy.capacity(), actualCopy.capacity());
        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    @Test
    public void updateWithFragmentedArray() throws Exception {

        byte[] dataInner = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);
        byte[] data = Arrays.copyOf(dataInner, dataInner.length * 2);
        MessageDigest mdSpi = newTestMD();
        mdSpi.update(data, 0, dataInner.length);
        byte[] actualDigest = mdSpi.digest();

        MessageDigest messageDigest = newReferenceMD();
        messageDigest.update(data, 0, dataInner.length);
        byte[] expectedDigest = messageDigest.digest();

        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    @Test
    public void updateWithLongArray() throws Exception {

        byte[] data = new byte[1024*1024];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte)i;
        }
        int rounds = 16;

        MessageDigest mdSpi = newTestMD();
        for (int i=0; i < rounds; i++) {
            mdSpi.update(data, 0, data.length);
        }
        byte[] actualDigest = mdSpi.digest();

        MessageDigest messageDigest = newReferenceMD();
        for (int i=0; i < rounds; i++) {
            messageDigest.update(data, 0, data.length);
        }
        byte[] expectedDigest = messageDigest.digest();

        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    @Test
    public void updateWithLongDirectBB() throws Exception {

        byte[] data = new byte[1024*1024];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte)i;
        }
        int rounds = 16;
        ByteBuffer direct = ByteBuffer.allocateDirect(data.length);
        direct.put(data);
        direct.flip();

        MessageDigest mdSpi = newTestMD();
        for (int i=0; i < rounds; i++) {
            mdSpi.update(direct);
            direct.flip();
        }
        byte[] actualDigest = mdSpi.digest();

        MessageDigest messageDigest = newReferenceMD();
        for (int i=0; i < rounds; i++) {
            messageDigest.update(direct);
            direct.flip();
        }
        byte[] expectedDigest = messageDigest.digest();

        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }
}
