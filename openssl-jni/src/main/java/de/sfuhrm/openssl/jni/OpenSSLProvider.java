package de.sfuhrm.openssl.jni;

import java.io.IOException;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JCA provider directing all calls to the system native OpenSSL library.
 * @author Stephan Fuhrmann
 */
public class OpenSSLProvider extends Provider {

    /** The provider name as passed to JCA. */
    public final static String PROVIDER_NAME = "OpenSSL";

    /** Constructor for the JCA Provider for OpenSSL JNI.
     * @throws IOException if the native object file can't be loaded and the
     * class can't be used.
     * */
    public OpenSSLProvider() throws IOException {
        super(PROVIDER_NAME, 1.0, "OpenSSL-JNI provider v1.0, implementing "
                + "multiple message digest algorithms.");

        Map<String,String> names = getNames();
        putAll(names);

        NativeLoader.loadAll();
    }

    /** Gets the names and the aliases of all message digest
     * algorithms.
     * @return a map mapping from algorithm name / alias to algorithm class.
     * */
    private static Map<String, String> getNames() {
        Map<String,String> result = getOpenSSLHashnames();
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
    private static Map<String, String> getOpenSSLHashnames() {
        Map<String, String> map = new HashMap<>();
        map.put("MessageDigest.MD5", MD5Native.class.getCanonicalName());
        map.put("MessageDigest.SHA1", SHA1Native.class.getCanonicalName());
        map.put("MessageDigest.SHA-224", SHA224Native.class.getCanonicalName());
        map.put("MessageDigest.SHA-256", SHA256Native.class.getCanonicalName());
        map.put("MessageDigest.SHA-384", SHA384Native.class.getCanonicalName());
        map.put("MessageDigest.SHA-512", SHA512Native.class.getCanonicalName());
        map.put("MessageDigest.SHA-512/224", SHA512_224Native.class.getCanonicalName());
        map.put("MessageDigest.SHA-512/256", SHA512_256Native.class.getCanonicalName());

        return map;
    }
}
