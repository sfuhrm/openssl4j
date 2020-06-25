package de.sfuhrm.openssl4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Test cases that compare the message digest of the
 * Sun provider (aka 'reference') with the implementations
 * in this context (aka 'test').
 * @author Stephan Fuhrmann
 */
public class MessageDigestWithReferenceMDTest extends BaseTest  {

    private static Stream<Arguments> provideTestArguments() throws NoSuchAlgorithmException, IOException {
        List<String> messageDigestNames = Arrays.asList("MD5", "SHA1", "SHA-224", "SHA-256", "SHA-384", "SHA-512", "SHA-512/224", "SHA-512/256", "SHA3-224", "SHA3-256", "SHA3-384", "SHA3-512");
        List<Arguments> result = new ArrayList<>();
        Provider openSsl = new OpenSSL4JProvider();
        Provider sun = MessageDigest.getInstance("MD5").getProvider();

            for (String messageDigestName : messageDigestNames) {
                    result.add(Arguments.of(
                            messageDigestName,
                            MessageDigest.getInstance(messageDigestName, openSsl),
                            MessageDigest.getInstance(messageDigestName, sun)));
            }

        return result.stream();
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void compareGetters(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        assertEquals(referenceMD.getAlgorithm(), testMD.getAlgorithm());
        assertEquals(referenceMD.getDigestLength(), testMD.getDigestLength());
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void digestWithNoData(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        byte[] actualDigest = testMD.digest();
        byte[] expectedDigest = referenceMD.digest();

        assertEquals(expectedDigest.length, actualDigest.length);
        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    private byte[] franzJagt() {
        byte[] data = "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes(ascii);
        return data;
    }

    private void applyTo(Consumer<MessageDigest> consumer, MessageDigest testMD, MessageDigest referenceMD) {
        consumer.accept(testMD);
        consumer.accept(referenceMD);
        byte[] actualDigest = testMD.digest();
        byte[] expectedDigest = referenceMD.digest();
        assertEquals(formatter.format(expectedDigest), formatter.format(actualDigest));
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithFullArray(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        applyTo(md -> md.update(franzJagt()), testMD, referenceMD);
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithSingleBytes(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        applyTo(md -> {
            for (byte val : franzJagt()) {
                md.update(val);
            }
        }, testMD, referenceMD);
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithHeapByteBuffer(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        final List<ByteBuffer> list = new ArrayList<>();
        applyTo(md -> {
            ByteBuffer bb = ByteBuffer.wrap(franzJagt());
            list.add(bb);
            md.update(bb);
        }, testMD, referenceMD);
        ByteBuffer first = list.get(0);
        ByteBuffer second = list.get(1);
        assertEquals(first.position(), second.position());
        assertEquals(first.limit(), second.limit());
        assertEquals(first.capacity(), second.capacity());
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithReadOnlyHeapByteBuffer(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        final List<ByteBuffer> list = new ArrayList<>();
        applyTo(md -> {
            ByteBuffer bb = ByteBuffer.wrap(franzJagt());
            bb = bb.asReadOnlyBuffer();
            list.add(bb);
            md.update(bb);
        }, testMD, referenceMD);
        ByteBuffer first = list.get(0);
        ByteBuffer second = list.get(1);
        assertEquals(first.position(), second.position());
        assertEquals(first.limit(), second.limit());
        assertEquals(first.capacity(), second.capacity());
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithReadOnlyMiddlePositionHeapByteBuffer(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        final List<ByteBuffer> list = new ArrayList<>();
        applyTo(md -> {
            ByteBuffer bb = ByteBuffer.wrap(franzJagt());
            bb = bb.asReadOnlyBuffer();
            bb.position(bb.remaining() / 2);
            list.add(bb);
            md.update(bb);
        }, testMD, referenceMD);
        ByteBuffer first = list.get(0);
        ByteBuffer second = list.get(1);
        assertEquals(first.position(), second.position());
        assertEquals(first.limit(), second.limit());
        assertEquals(first.capacity(), second.capacity());
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithDirectByteBufferNoRemaining(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        final List<ByteBuffer> list = new ArrayList<>();
        applyTo(md -> {
            ByteBuffer bb = ByteBuffer.allocateDirect(franzJagt().length);
            bb.put(franzJagt());
            list.add(bb);
            md.update(bb);
        }, testMD, referenceMD);
        ByteBuffer first = list.get(0);
        ByteBuffer second = list.get(1);
        assertEquals(first.position(), second.position());
        assertEquals(first.limit(), second.limit());
        assertEquals(first.capacity(), second.capacity());
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithDirectByteBuffer(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        final List<ByteBuffer> list = new ArrayList<>();
        applyTo(md -> {
            ByteBuffer bb = ByteBuffer.allocateDirect(franzJagt().length);
            bb.put(franzJagt());
            bb.flip();
            list.add(bb);
            md.update(bb);
        }, testMD, referenceMD);
        ByteBuffer first = list.get(0);
        ByteBuffer second = list.get(1);
        assertEquals(first.position(), second.position());
        assertEquals(first.limit(), second.limit());
        assertEquals(first.capacity(), second.capacity());
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithNonFullDirectByteBuffer(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        final List<ByteBuffer> list = new ArrayList<>();
        applyTo(md -> {
            ByteBuffer bb = ByteBuffer.allocateDirect(franzJagt().length * 2);
            bb.put(franzJagt());
            bb.flip();
            list.add(bb);
            md.update(bb);
        }, testMD, referenceMD);
        ByteBuffer first = list.get(0);
        ByteBuffer second = list.get(1);
        assertEquals(first.position(), second.position());
        assertEquals(first.limit(), second.limit());
        assertEquals(first.capacity(), second.capacity());
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithMiddlePositionDirectByteBuffer(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        final List<ByteBuffer> list = new ArrayList<>();
        applyTo(md -> {
            ByteBuffer bb = ByteBuffer.allocateDirect(franzJagt().length);
            bb.put(franzJagt());
            bb.flip();
            bb.position(bb.remaining() / 2);
            list.add(bb);
            md.update(bb);
        }, testMD, referenceMD);
        ByteBuffer first = list.get(0);
        ByteBuffer second = list.get(1);
        assertEquals(first.position(), second.position());
        assertEquals(first.limit(), second.limit());
        assertEquals(first.capacity(), second.capacity());
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithFragmentedArray(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        applyTo(md -> {
            byte[] dataInner = franzJagt();
            byte[] data = new byte[dataInner.length * 2];
            int insertOffset = data.length / 4;
            System.arraycopy(dataInner, 0, data, insertOffset, dataInner.length);
            md.update(data, insertOffset, dataInner.length);
        }, testMD, referenceMD);
    }

    byte[] filledArray(int size) {
        byte[] data = new byte[size];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) i;
        }
        return data;
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithLongArray(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        applyTo(md -> {
            byte[] data = filledArray(1024 * 1024);
            int rounds = 16;
            for (int i=0; i < rounds; i++) {
                md.update(data, 0, data.length);
            }
        }, testMD, referenceMD);
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithLongDirectBB(String digestName, MessageDigest testMD, MessageDigest referenceMD) {
        applyTo(md -> {
            byte[] data = filledArray(1024 * 1024);
            int rounds = 16;
            ByteBuffer direct = ByteBuffer.allocateDirect(data.length);
            direct.put(data);
            direct.flip();
            for (int i=0; i < rounds; i++) {
                md.update(direct);
            }
        }, testMD, referenceMD);
    }
}
