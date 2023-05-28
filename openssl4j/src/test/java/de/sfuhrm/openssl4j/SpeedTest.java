package de.sfuhrm.openssl4j;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Benchmark testing the speed of multiple algorithm implementations.
 * @author Stephan Fuhrmann
 */
//@Disabled
public class SpeedTest {

    static final int TIMES = 100;

    private static Stream<Arguments> provideTestArguments() throws NoSuchAlgorithmException, IOException {
        List<String> messageDigestNames = Arrays.asList("MD5", "SHA1", "SHA-224", "SHA-256", "SHA-384", "SHA-512", "SHA-512/224", "SHA-512/256", "SHA3-224", "SHA3-256", "SHA3-384", "SHA3-512");
        List<Integer> bufferSizes = Arrays.asList(1000, 100000, 1000000);
        List<Arguments> result = new ArrayList<>();
        Map<String, Provider> providerMap = new HashMap<>();
        providerMap.put("OpenSSL", new OpenSSL4JProvider());
        providerMap.put("Sun", Security.getProvider("SUN"));
        providerMap.put("BC", new BouncyCastleProvider());

        for (Map.Entry<String, Provider> providerEntry : providerMap.entrySet()) {
            for (String messageDigestName : messageDigestNames) {
                for (Integer bufferSize : bufferSizes) {
                    String name = providerEntry.getKey();
                    result.add(Arguments.of(
                            name,
                            messageDigestName,
                            MessageDigest.getInstance(messageDigestName, providerEntry.getValue()),
                            messageDigestName,
                            bufferSize));
                }
            }
        }

        return result.stream();
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithByte(String provider, String messageDigest, MessageDigest md, String messageDigestName, Integer bufferSize) {
        benchmark(provider, messageDigest, "SingleByte", TIMES, bufferSize, () -> {
            for (int i = 0; i < bufferSize; i++) {
                md.update((byte) 0);
            }
        });
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithArray(String provider, String messageDigest, MessageDigest md, String messageDigestName, Integer bufferSize) {
        byte[] data = new byte[bufferSize];
        benchmark(provider, messageDigest, "ByteArray", TIMES, bufferSize, () -> md.update(data));
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithHeapBB(String provider, String messageDigest, MessageDigest md, String messageDigestName, Integer bufferSize) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
        byteBuffer.limit(byteBuffer.capacity());
        benchmark(provider, messageDigest, "HeapBB", TIMES, bufferSize, () -> {
            md.update(byteBuffer);
            byteBuffer.flip();
        });
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithDirectBB(String provider, String messageDigest, MessageDigest md, String messageDigestName, Integer bufferSize) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize);
        byteBuffer.limit(byteBuffer.capacity());
        benchmark(provider, messageDigest, "DirectBB", TIMES, bufferSize, () -> {
            md.update(byteBuffer);
            byteBuffer.flip();
        });
    }

    static boolean first = true;
    static void benchmark(String provider, String messageDigest, String testName, int times, int length, Runnable r) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            r.run();
        }
        long end = System.currentTimeMillis();
        long millis = end - start;

        double totalData = times * length;
        double seconds = millis / 1000.;

        Formatter formatter = new Formatter(System.out, Locale.ENGLISH);

        if (first) {
            formatter.format("Provider;MD;Test;Times;Length;Seconds;Data;SpeedMBPS%n");
            first = false;
        }

        formatter.format("%s;%s;%s;%d;%d;%g;%g;%g%n",
                provider,
                messageDigest,
                testName,
                times,
                length,
                seconds,
                totalData,
                (totalData / (1024. * 1024.)) / seconds);
    }
}
