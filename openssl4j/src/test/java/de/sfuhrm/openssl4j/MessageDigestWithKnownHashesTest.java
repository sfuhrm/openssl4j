package de.sfuhrm.openssl4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases that compare the message digest of the
 * implementations with well known expected outputs.
 * @author Stephan Fuhrmann
 */
public class MessageDigestWithKnownHashesTest {

    private Formatter formatter;

    @BeforeEach
    public void init() {
        formatter = Formatter.getInstance();
    }

    private static final String[] REFERENCES = new String[] {
        "BLAKE2b512", "", "786a02f742015903c6c6fd852552d272912f4740e15847618a86e217f71f5419d25e1031afee585313896444934eb04b903a685b1448b755d56f701afe9be2ce",
        "BLAKE2b512", "The quick brown fox jumps over the lazy dog", "a8add4bdddfd93e4877d2746e62817b116364a1fa7bc148d95090bc7333b3673f82401cf7aa2e4cb1ecd90296e3f14cb5413f8ed77be73045b13914cdcd6a918",
        "BLAKE2b512", "The quick brown fox jumps over the lazy dof", "ab6b007747d8068c02e25a6008db8a77c218d94f3b40d2291a7dc8a62090a744c082ea27af01521a102e42f480a31e9844053f456b4b41e8aa78bbe5c12957bb",
        "BLAKE2b512", "", "786a02f742015903c6c6fd852552d272912f4740e15847618a86e217f71f5419d25e1031afee585313896444934eb04b903a685b1448b755d56f701afe9be2ce",
        "BLAKE2s256", "", "69217a3079908094e11121d042354a7c1f55b6482ca1a51e1b250dfd1ed0eef9",
        "RIPEMD160", "", "9c1185a5c5e9fc54612808977ee8f548b2258d31",
        "RIPEMD160", "The quick brown fox jumps over the lazy dog", "37f332f68db77bd9d7edd4969571ad671cf9dd3b",
        "RIPEMD160", "The quick brown fox jumps over the lazy cog", "132072df690933835eb8b6ad0b77e7b6f14acad7",
        "MD4", "", "31d6cfe0d16ae931b73c59d7e0c089c0",
        "MD4", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", "043f8582f241db351ce627e153e7f0e4",
        "Whirlpool", "", "19FA61D75522A4669B44E39C1D2E1726C530232130D407F89AFEE0964997F7A73E83BE698B288FEBCF88E3E03C4F0757EA8964E59B63D93708B138CC42A66EB3",
        "Whirlpool", "The quick brown fox jumps over the lazy eog", "C27BA124205F72E6847F3E19834F925CC666D0974167AF915BB462420ED40CC50900D85A1F923219D832357750492D5C143011A76988344C2635E69D06F2D38C"
    };

    private static Stream<Arguments> provideTestArguments() throws NoSuchAlgorithmException, IOException {
        List<Arguments> result = new ArrayList<>();
        Provider openSsl = new OpenSSL4JProvider();

        for (int i=0; i < REFERENCES.length; i+= 3) {
            String algorithm = REFERENCES[i];
            String clearText = REFERENCES[i + 1];
            String expected =  REFERENCES[i + 2];

            result.add(Arguments.of(
                    algorithm,
                    MessageDigest.getInstance(algorithm, openSsl),
                    clearText.getBytes(StandardCharsets.US_ASCII),
                    expected
                    ));
        }

        return result.stream();
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    public void compareWithWellKnownHash(String digestName, MessageDigest testMD, byte[] clearText, String expectedDigest) {
        testMD.update(clearText);
        byte[] actual = testMD.digest();

        assertEquals(expectedDigest.toLowerCase(), formatter.format(actual));
    }
}
