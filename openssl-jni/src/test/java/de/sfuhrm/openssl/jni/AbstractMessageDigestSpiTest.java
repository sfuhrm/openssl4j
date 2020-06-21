package de.sfuhrm.openssl.jni;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.MessageDigestSpi;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Abstract test base for a {@linkplain MessageDigestSpi} implementation.
 * @author Stephan Fuhrmann
 */
public abstract class AbstractMessageDigestSpiTest extends BasicTest {

    protected MessageDigestSpiAdapter newTestAdapter() throws NoSuchMethodException {
        return new MessageDigestSpiAdapter(newTestMD());
    }

    /** Create a new test message digest that we're testing against a
     * {@link #newReferenceMD() reference} implementation.
     * */
    protected abstract MessageDigestSpi newTestMD();

    /** Create a new reference message digest that is known to work.
     *  */
    protected abstract MessageDigest newReferenceMD() throws NoSuchAlgorithmException;

    @Test
    public void digestWithNoData() throws Exception {
        MessageDigestSpiAdapter mdSpi = newTestAdapter();

        mdSpi.engineReset();
        byte[] actualDigest = mdSpi.engineDigest();

        MessageDigest reference = newReferenceMD();
        byte[] expectedDigest = reference.digest();

        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    @Test
    public void digestWithReset() throws Exception {
        MessageDigestSpiAdapter mdSpi = newTestAdapter();
        mdSpi.engineReset();

        byte[] data = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);

        mdSpi.engineUpdate(data, 0, data.length);
        byte[] actualDigest = mdSpi.engineDigest();

        MessageDigest reference = newReferenceMD();
        byte[] expectedDigest = reference.digest(data);

        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));

        mdSpi.engineReset();
        mdSpi.engineUpdate(data, 0, data.length);
        byte[] expectedDigest2 = mdSpi.engineDigest();
        assertEquals(formatter.format(expectedDigest2), formatter.format(actualDigest));
    }

    @Test
    public void updateWithFullArray() throws Exception {
        MessageDigestSpiAdapter mdSpi = newTestAdapter();
        mdSpi.engineReset();

        byte[] data = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);

        mdSpi.engineUpdate(data, 0, data.length);
        byte[] actualDigest = mdSpi.engineDigest();

        MessageDigest reference = newReferenceMD();
        byte[] expectedDigest = reference.digest(data);

        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    @Test
    public void updateWithSingleBytes() throws Exception {
        MessageDigestSpiAdapter mdSpi = newTestAdapter();
        mdSpi.engineReset();

        byte[] data = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);

        for (byte val : data) {
            mdSpi.engineUpdate(val);
        }
        byte actualDigest[] = mdSpi.engineDigest();

        MessageDigest reference = newReferenceMD();
        for (byte val : data) {
            reference.update(val);
        }
        byte[] expectedDigest = reference.digest();
        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    @Test
    public void updateWithHeapByteBuffer() throws Exception {
        MessageDigestSpiAdapter mdSpi = newTestAdapter();
        mdSpi.engineReset();

        byte[] data = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);
        ByteBuffer bb = ByteBuffer.wrap(data);

        ByteBuffer actualCopy = bb.duplicate();
        mdSpi.engineUpdate(actualCopy);
        byte actualDigest[] = mdSpi.engineDigest();

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
        MessageDigestSpiAdapter mdSpi = newTestAdapter();
        mdSpi.engineReset();

        byte[] data = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length);
        bb.put(data);
        bb.flip();

        ByteBuffer actualCopy = bb.duplicate();
        mdSpi.engineUpdate(actualCopy);
        byte actualDigest[] = mdSpi.engineDigest();

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
        MessageDigestSpiAdapter mdSpi = newTestAdapter();
        mdSpi.engineReset();

        byte[] dataInner = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);
        byte[] data = Arrays.copyOf(dataInner, dataInner.length * 2);
        mdSpi.engineUpdate(data, 0, dataInner.length);
        byte[] actualDigest = mdSpi.engineDigest();

        MessageDigest messageDigest = newReferenceMD();
        messageDigest.update(data, 0, dataInner.length);
        byte[] expectedDigest = messageDigest.digest();

        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

}
