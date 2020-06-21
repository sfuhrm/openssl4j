package de.sfuhrm.openssl.jni;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.MessageDigestSpi;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Benchmark testing the speed of multiple algorithm implementations.
 * @author Stephan Fuhrmann
 */
public class SpeedTest {

    private static Provider sun;
    private static Provider ssl;

    @BeforeAll
    protected static void init() throws IOException, NoSuchAlgorithmException {
        sun = MessageDigest.getInstance("MD5").getProvider();
        ssl = new OpenSSLProvider();
    }

    static final int TIMES = 100;

    private static Stream<Arguments> provideTestArguments() throws NoSuchAlgorithmException {
        List<String> messageDigestNames = Arrays.asList("MD5");
        List<Integer> bufferSizes = Arrays.asList(1000, 100000, 1000000);
        List<Arguments> result = new ArrayList<>();
        Map<String, Provider> providerMap = new HashMap<>();
        providerMap.put("OpenSSL", ssl);
        providerMap.put("Sun", sun);

        for (Map.Entry<String, Provider> providerEntry : providerMap.entrySet()) {
            for (String messageDigestName : messageDigestNames) {
                for (Integer bufferSize : bufferSizes) {
                    String name = providerEntry.getKey()+"-"+messageDigestName;
                    result.add(Arguments.of(
                            name,
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
    public void updateWithByte(String benchmarkName, MessageDigest md, String messageDigestName, Integer bufferSize) throws NoSuchAlgorithmException {
        benchmark(benchmarkName, "SingleByte", TIMES, bufferSize, () -> {
            for (int i = 0; i < bufferSize; i++) {
                md.update((byte) 0);
            }
        });
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithArray(String benchmarkName, MessageDigest md, String messageDigestName, Integer bufferSize) throws NoSuchAlgorithmException {
        byte[] data = new byte[bufferSize];
        benchmark(benchmarkName, "ByteArray", TIMES, bufferSize, () -> {
                md.update(data);
        });
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithHeapBB(String benchmarkName, MessageDigest md, String messageDigestName, Integer bufferSize) throws NoSuchAlgorithmException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
        byteBuffer.limit(byteBuffer.capacity());
        benchmark(benchmarkName, "HeapBB", TIMES, bufferSize, () -> {
            md.update(byteBuffer);
            byteBuffer.flip();
        });
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void updateWithDirectBB(String benchmarkName, MessageDigest md, String messageDigestName, Integer bufferSize) throws NoSuchAlgorithmException {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize);
        byteBuffer.limit(byteBuffer.capacity());
        benchmark(benchmarkName, "DirectBB", TIMES, bufferSize, () -> {
            md.update(byteBuffer);
            byteBuffer.flip();
        });
    }

    static void benchmark(String benchmarkName, String testName, int times, int length, Runnable r) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            r.run();
        }
        long end = System.currentTimeMillis();
        long millis = end - start;

        double totalData = times * length;
        double seconds = millis / 1000.;

        System.out.printf("Bench;Test;Times;Length;Seconds;Data;SpeedMBPS%n");

        System.out.printf("%s;%s;%d;%d;%g;%g;%g%n",
                benchmarkName,
                testName,
                times,
                length,
                seconds,
                totalData,
                (totalData / (1024. * 1024.)) / seconds);
    }
}
