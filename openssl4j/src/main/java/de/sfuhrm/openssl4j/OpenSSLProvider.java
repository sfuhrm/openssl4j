package de.sfuhrm.openssl4j;

import java.io.IOException;
import java.security.Provider;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JCA provider directing all calls to the system native OpenSSL library.
 * @author Stephan Fuhrmann
 */
public class OpenSSLProvider extends Provider {

    private static final String VERSION = "0.1";

    /** The provider name as passed to JCA. */
    public final static String PROVIDER_NAME = "OpenSSL";

    private static Set<String> openSslMessageDigestAlgorithms;

    /** Constructor for the JCA Provider for OpenSSL JNI.
     * @throws IOException if the native object file can't be loaded and the
     * class can't be used.
     * */
    public OpenSSLProvider() throws IOException {
        super(PROVIDER_NAME, VERSION, "OpenSSL4J provider v" + VERSION + ", implementing "
                + "multiple message digest algorithms.");

        NativeLoader.loadAll();
        if (openSslMessageDigestAlgorithms == null) {
            openSslMessageDigestAlgorithms = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(OpenSSLMessageDigestNative.listMessageDigests())));
        }

        Map<String,String> names = getNames(openSslMessageDigestAlgorithms);
        putAll(names);
    }

    /** Gets the names and the aliases of all message digest
     * algorithms.
     * @return a map mapping from algorithm name / alias to algorithm class.
     * */
    private static Map<String, String> getNames(Set<String> available) {
        Map<String,String> result = getOpenSSLHashnames(available);
        result.putAll(createAliases(result));
        return result;
    }

    /** Creates some aliases for an input map.
     * @param map a map with keys being algorithm names of the form "MessageDigest.MD5"
     *            and the keys being java class names.
     * @return a map mapping from algorithm name / alias to algorithm class.
     * */
    private static Map<String,String> createAliases(Map<String, String> map) {
        Map<String, String> aliases = new HashMap<>();
        Pattern pattern = Pattern.compile("([^0-9]*)-([0-9]+)");

        for (Map.Entry<String,String> entry : map.entrySet()) {
            Matcher matcher = pattern.matcher(entry.getKey());
            if (matcher.matches()) {

                // adds for MessageDigest.SHA512 an alias like MessageDigest.SHA-512
                aliases.put(
                        matcher.group(1) + matcher.group(2),
                        entry.getValue());
            }
        }
        aliases.put("MessageDigest.SHA", map.get("MessageDigest.SHA1"));
        return aliases;
    }

    /** Name pairs mapping from SSL to Java.
     * First one is SSL name, second one is Java name.
     * */
    private static String[] SSL_TO_JAVA_NAMES = {
            "MD5", "MD5",
            "SHA1", "SHA1",
            "SHA224", "SHA-224",
            "SHA256", "SHA-256",
            "SHA384", "SHA-384",
            "SHA512", "SHA-512",
            "SHA512-224", "SHA-512/224",
            "SHA512-256", "SHA-512/256",
            "SHA3-224", "SHA3-224",
            "SHA3-256", "SHA3-256",
            "SHA3-384", "SHA3-384",
            "SHA3-512", "SHA3-512",
            "SHA3-512", "SHA3-512",
            "BLAKE2b512", "BLAKE2b512",
            "BLAKE2s256", "BLAKE2s256",
            "MD4", "MD4",
            "RIPEMD160", "RIPEMD160",
            "SM3", "SM3",
            "whirlpool", "Whirlpool"
    };

    /** Fills a map with the names of all algorithms in
     * OpenSSL-JNA.
     * @return mapping from algorithm name to class name.
     * */
    private static Map<String, String> getOpenSSLHashnames(Set<String> available) {
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < SSL_TO_JAVA_NAMES.length; i+= 2) {
            String sslName = SSL_TO_JAVA_NAMES[i + 0];
            String javaName = SSL_TO_JAVA_NAMES[i + 1];

            // only if OpenSSL has the algorithm available, add it
            if (available.contains(sslName)) {
                String javaClass = OpenSSL.class.getName() + "$" +
                        (javaName.replaceAll("-", "_").replaceAll("/", "_"));
                map.put("MessageDigest." + javaName, javaClass);
            }
        }

        return map;
    }
}
