package de.sfuhrm.openssl.jni;

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

    /** The provider name as passed to JCA. */
    public final static String PROVIDER_NAME = "OpenSSL";

    private static Set<String> openSslMessageDigestAlgorithms;

    /** Constructor for the JCA Provider for OpenSSL JNI.
     * @throws IOException if the native object file can't be loaded and the
     * class can't be used.
     * */
    public OpenSSLProvider() throws IOException {
        super(PROVIDER_NAME, 1.0, "OpenSSL-JNI provider v1.0, implementing "
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
        Pattern pattern = Pattern.compile("([^0-9]*)([0-9]+)");
        for (Map.Entry<String,String> entry : map.entrySet()) {
            Matcher matcher = pattern.matcher(entry.getKey());
            if (matcher.matches()) {

                // adds for MessageDigest.SHA512 an alias like MessageDigest.SHA-512
                aliases.put(
                        matcher.group(1)+
                                "-"+
                                matcher.group(2), entry.getValue());
            }
        }
        return aliases;
    }

    /** Fills a map with the names of all algorithms in
     * OpenSSL-JNA.
     * @return mapping from algorithm name to class name.
     * */
    private static Map<String, String> getOpenSSLHashnames(Set<String> available) {
        Map<String, String> map = new HashMap<>();

        if (available.contains("MD5")) {
            map.put("MessageDigest.MD5", MD5Native.class.getCanonicalName());
        }
        if (available.contains("SHA1")) {
            map.put("MessageDigest.SHA1", SHA1Native.class.getCanonicalName());
        }
        if (available.contains("SHA224")) {
            map.put("MessageDigest.SHA-224", SHA224Native.class.getCanonicalName());
        }
        if (available.contains("SHA256")) {
            map.put("MessageDigest.SHA-256", SHA256Native.class.getCanonicalName());
        }
        if (available.contains("SHA384")) {
            map.put("MessageDigest.SHA-384", SHA384Native.class.getCanonicalName());
        }
        if (available.contains("SHA512")) {
            map.put("MessageDigest.SHA-512", SHA512Native.class.getCanonicalName());
        }
        if (available.contains("SHA512-224")) {
            map.put("MessageDigest.SHA-512/224", SHA512_224Native.class.getCanonicalName());
        }
        if (available.contains("SHA512-256")) {
            map.put("MessageDigest.SHA-512/256", SHA512_256Native.class.getCanonicalName());
        }

        if (available.contains("SHA3-224")) {
            map.put("MessageDigest.SHA3-224", SHA3_224Native.class.getCanonicalName());
        }
        if (available.contains("SHA3-256")) {
            map.put("MessageDigest.SHA3-256", SHA3_256Native.class.getCanonicalName());
        }
        if (available.contains("SHA3-384")) {
            map.put("MessageDigest.SHA3-384", SHA3_384Native.class.getCanonicalName());
        }
        if (available.contains("SHA3-512")) {
            map.put("MessageDigest.SHA3-512", SHA3_512Native.class.getCanonicalName());
        }

        return map;
    }
}
