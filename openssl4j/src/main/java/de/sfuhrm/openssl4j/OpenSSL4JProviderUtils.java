package de.sfuhrm.openssl4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenSSL4JProviderUtils {

  private OpenSSL4JProviderUtils() {}

  static final boolean DEBUG = false;

  /**
   * Name pairs mapping from OpenSSL message-digest algorithm names to Java lookup names. First one
   * is message-digest algorithm name, second one is Java lookup name.
   */
  static final String[] MESSAGE_DIGEST_ALGORITHM_NAMES_TO_JAVA_LOOKUP_NAMES = {
    "BLAKE2b512", "BLAKE2b512",
    "BLAKE2s256", "BLAKE2s256",
    "MD4", "MD4",
    "MD5", "MD5",
    "RIPEMD160", "RIPEMD160",
    "SHA1", "SHA1",
    "SHA224", "SHA-224",
    "SHA256", "SHA-256",
    "SHA3-224", "SHA3-224",
    "SHA3-256", "SHA3-256",
    "SHA3-384", "SHA3-384",
    "SHA3-512", "SHA3-512",
    "SHA384", "SHA-384",
    "SHA512", "SHA-512",
    "SHA512-224", "SHA-512/224",
    "SHA512-256", "SHA-512/256",
    "SM3", "SM3",
    "whirlpool", "Whirlpool"
  };

  static Set<String> openSslMessageDigestAlgorithms;

  /**
   * Fills a map with the names of all algorithms in OpenSSL-JNA.
   *
   * @return mapping from algorithm name to class name.
   */
  static Map<String, String> getOpenSSLMessageDigestAlgorithmsToImplementations(
      Set<String> availableOpenSslMessageDigestAlgorithms) {
    Map<String, String> map = new HashMap<>();

    for (int i = 0; i < MESSAGE_DIGEST_ALGORITHM_NAMES_TO_JAVA_LOOKUP_NAMES.length; i += 2) {
      String sslName = MESSAGE_DIGEST_ALGORITHM_NAMES_TO_JAVA_LOOKUP_NAMES[i];
      String javaName = MESSAGE_DIGEST_ALGORITHM_NAMES_TO_JAVA_LOOKUP_NAMES[i + 1];

      // Only if OpenSSL has the algorithm available, add it.
      if (availableOpenSslMessageDigestAlgorithms.contains(sslName)) {
        String javaClass =
            MessageDigest.class.getName() + "$" + (javaName.replace("-", "_").replace("/", "_"));
        map.put("MessageDigest." + javaName, javaClass);
      }
    }

    return map;
  }

  static final Pattern MESSAGE_DIGEST_ALIAS_PATTERN = Pattern.compile("([^0-9]*)-([0-9]+)");

  /**
   * Creates some aliases for an input map. The motivation is that the Sun provider accepts, for
   * example, "SHA1" and "SHA-1", and this library needs to stay API compatible as much as possible
   * in order to be a drop-in replacement for the Sun provider.
   *
   * @param map a map with keys being algorithm names of the form "MessageDigest.MD5" and the keys
   *     being Java class names.
   * @return a map mapping from algorithm name / alias to algorithm class.
   */
  static Map<String, String> createMessageDigestAliases(Map<String, String> map) {
    Map<String, String> aliases = new HashMap<>();

    for (Map.Entry<String, String> entry : map.entrySet()) {
      Matcher matcher = MESSAGE_DIGEST_ALIAS_PATTERN.matcher(entry.getKey());
      if (matcher.matches()) {

        // Adds for MessageDigest.SHA512 an alias like MessageDigest.SHA-512.
        aliases.put(matcher.group(1) + matcher.group(2), entry.getValue());
      }
    }
    aliases.put("MessageDigest.SHA", map.get("MessageDigest.SHA1"));
    return aliases;
  }

  /**
   * Gets the names and the aliases of all message-digest algorithms.
   *
   * @return a map mapping from algorithm name/alias to message-digest algorithm class.
   */
  static Map<String, String> getMessageDigestAlgorithmNames(
      Set<String> availableOpenSslMessageDigestAlgorithmNames) {
    Map<String, String> result =
        getOpenSSLMessageDigestAlgorithmsToImplementations(
            availableOpenSslMessageDigestAlgorithmNames);
    result.putAll(createMessageDigestAliases(result));
    return result;
  }

  /**
   * Name pairs mapping from OpenSSL cipher names to Java lookup names. First one is cipher name,
   * second one is Java lookup name.
   */
  static final String[] CIPHER_NAMES_TO_JAVA_LOOKUP_NAMES = {
    "AES-128-CBC", "AES-128-CBC",
    "AES-128-CFB", "AES-128-CFB",
    "AES-128-CFB1", "AES-128-CFB1",
    "AES-128-CFB8", "AES-128-CFB8",
    "AES-128-CTR", "AES-128-CTR",
    "AES-128-ECB", "AES-128-ECB",
    "AES-128-OCB", "AES-128-OCB",
    "AES-128-OFB", "AES-128-OFB",
    "AES-128-XTS", "AES-128-XTS",
    "AES-192-CBC", "AES-192-CBC",
    "AES-192-CFB", "AES-192-CFB",
    "AES-192-CFB1", "AES-192-CFB1",
    "AES-192-CFB8", "AES-192-CFB8",
    "AES-192-CTR", "AES-192-CTR",
    "AES-192-ECB", "AES-192-ECB",
    "AES-192-OCB", "AES-192-OCB",
    "AES-192-OFB", "AES-192-OFB",
    "AES-256-CBC", "AES-256-CBC",
    "AES-256-CFB", "AES-256-CFB",
    "AES-256-CFB1", "AES-256-CFB1",
    "AES-256-CFB8", "AES-256-CFB8",
    "AES-256-CTR", "AES-256-CTR",
    "AES-256-ECB", "AES-256-ECB",
    "AES-256-OCB", "AES-256-OCB",
    "AES-256-OFB", "AES-256-OFB",
    "AES-256-XTS", "AES-256-XTS",
    "ARIA-128-CBC", "ARIA-128-CBC",
    "ARIA-128-CCM", "ARIA-128-CCM",
    "ARIA-128-CFB", "ARIA-128-CFB",
    "ARIA-128-CFB1", "ARIA-128-CFB1",
    "ARIA-128-CFB8", "ARIA-128-CFB8",
    "ARIA-128-CTR", "ARIA-128-CTR",
    "ARIA-128-ECB", "ARIA-128-ECB",
    "ARIA-128-GCM", "ARIA-128-GCM",
    "ARIA-128-OFB", "ARIA-128-OFB",
    "ARIA-192-CBC", "ARIA-192-CBC",
    "ARIA-192-CCM", "ARIA-192-CCM",
    "ARIA-192-CFB", "ARIA-192-CFB",
    "ARIA-192-CFB1", "ARIA-192-CFB1",
    "ARIA-192-CFB8", "ARIA-192-CFB8",
    "ARIA-192-CTR", "ARIA-192-CTR",
    "ARIA-192-ECB", "ARIA-192-ECB",
    "ARIA-192-GCM", "ARIA-192-GCM",
    "ARIA-192-OFB", "ARIA-192-OFB",
    "ARIA-256-CBC", "ARIA-256-CBC",
    "ARIA-256-CCM", "ARIA-256-CCM",
    "ARIA-256-CFB", "ARIA-256-CFB",
    "ARIA-256-CFB1", "ARIA-256-CFB1",
    "ARIA-256-CFB8", "ARIA-256-CFB8",
    "ARIA-256-CTR", "ARIA-256-CTR",
    "ARIA-256-ECB", "ARIA-256-ECB",
    "ARIA-256-GCM", "ARIA-256-GCM",
    "ARIA-256-OFB", "ARIA-256-OFB",
    "BF-CBC", "BF-CBC",
    "BF-CFB", "BF-CFB",
    "BF-ECB", "BF-ECB",
    "BF-OFB", "BF-OFB",
    "CAMELLIA-128-CBC", "CAMELLIA-128-CBC",
    "CAMELLIA-128-CFB", "CAMELLIA-128-CFB",
    "CAMELLIA-128-CFB1", "CAMELLIA-128-CFB1",
    "CAMELLIA-128-CFB8", "CAMELLIA-128-CFB8",
    "CAMELLIA-128-CTR", "CAMELLIA-128-CTR",
    "CAMELLIA-128-ECB", "CAMELLIA-128-ECB",
    "CAMELLIA-128-OFB", "CAMELLIA-128-OFB",
    "CAMELLIA-192-CBC", "CAMELLIA-192-CBC",
    "CAMELLIA-192-CFB", "CAMELLIA-192-CFB",
    "CAMELLIA-192-CFB1", "CAMELLIA-192-CFB1",
    "CAMELLIA-192-CFB8", "CAMELLIA-192-CFB8",
    "CAMELLIA-192-CTR", "CAMELLIA-192-CTR",
    "CAMELLIA-192-ECB", "CAMELLIA-192-ECB",
    "CAMELLIA-192-OFB", "CAMELLIA-192-OFB",
    "CAMELLIA-256-CBC", "CAMELLIA-256-CBC",
    "CAMELLIA-256-CFB", "CAMELLIA-256-CFB",
    "CAMELLIA-256-CFB1", "CAMELLIA-256-CFB1",
    "CAMELLIA-256-CFB8", "CAMELLIA-256-CFB8",
    "CAMELLIA-256-CTR", "CAMELLIA-256-CTR",
    "CAMELLIA-256-ECB", "CAMELLIA-256-ECB",
    "CAMELLIA-256-OFB", "CAMELLIA-256-OFB",
    "CAST5-CBC", "CAST5-CBC",
    "CAST5-CFB", "CAST5-CFB",
    "CAST5-ECB", "CAST5-ECB",
    "CAST5-OFB", "CAST5-OFB",
    "ChaCha20", "ChaCha20",
    "ChaCha20-Poly1305", "ChaCha20-Poly1305",
    "DES-CBC", "DES-CBC",
    "DES-CFB", "DES-CFB",
    "DES-CFB1", "DES-CFB1",
    "DES-CFB8", "DES-CFB8",
    "DES-ECB", "DES-ECB",
    "DES-EDE", "DES-EDE",
    "DES-EDE-CBC", "DES-EDE-CBC",
    "DES-EDE-CFB", "DES-EDE-CFB",
    "DES-EDE-OFB", "DES-EDE-OFB",
    "DES-EDE3", "DES-EDE3",
    "DES-EDE3-CBC", "DES-EDE3-CBC",
    "DES-EDE3-CFB", "DES-EDE3-CFB",
    "DES-EDE3-CFB1", "DES-EDE3-CFB1",
    "DES-EDE3-CFB8", "DES-EDE3-CFB8",
    "DES-EDE3-OFB", "DES-EDE3-OFB",
    "DES-OFB", "DES-OFB",
    "DESX-CBC", "DESX-CBC",
    "IDEA-CBC", "IDEA-CBC",
    "IDEA-CFB", "IDEA-CFB",
    "IDEA-ECB", "IDEA-ECB",
    "IDEA-OFB", "IDEA-OFB",
    "RC2-40-CBC", "RC2-40-CBC",
    "RC2-64-CBC", "RC2-64-CBC",
    "RC2-CBC", "RC2-CBC",
    "RC2-CFB", "RC2-CFB",
    "RC2-ECB", "RC2-ECB",
    "RC2-OFB", "RC2-OFB",
    "RC4", "RC4",
    "RC4-40", "RC4-40",
    "RC4-HMAC-MD5", "RC4-HMAC-MD5",
    "SEED-CBC", "SEED-CBC",
    "SEED-CFB", "SEED-CFB",
    "SEED-ECB", "SEED-ECB",
    "SEED-OFB", "SEED-OFB",
    "SM4-CBC", "SM4-CBC",
    "SM4-CFB", "SM4-CFB",
    "SM4-CTR", "SM4-CTR",
    "SM4-ECB", "SM4-ECB",
    "SM4-OFB", "SM4-OFB",
    "id-aes128-CCM", "id-aes128-CCM",
    "id-aes128-GCM", "id-aes128-GCM",
    "id-aes128-wrap", "id-aes128-wrap",
    "id-aes128-wrap-pad", "id-aes128-wrap-pad",
    "id-aes192-CCM", "id-aes192-CCM",
    "id-aes192-GCM", "id-aes192-GCM",
    "id-aes192-wrap", "id-aes192-wrap",
    "id-aes192-wrap-pad", "id-aes192-wrap-pad",
    "id-aes256-CCM", "id-aes256-CCM",
    "id-aes256-GCM", "id-aes256-GCM",
    "id-aes256-wrap", "id-aes256-wrap",
    "id-aes256-wrap-pad", "id-aes256-wrap-pad",
    "id-smime-alg-CMS3DESwrap", "id-smime-alg-CMS3DESwrap"
  };

  static Set<String> openSslCiphers;

  /**
   * Fills a map with the names of all ciphers in OpenSSL-JNA.
   *
   * @return mapping from cipher name to class name.
   */
  static Map<String, String> getOpenSSLCiphersToImplementations(
      Set<String> availableOpenSslCiphers) {
    Map<String, String> map = new HashMap<>();

    for (int i = 0; i < CIPHER_NAMES_TO_JAVA_LOOKUP_NAMES.length; i += 2) {
      String sslName = CIPHER_NAMES_TO_JAVA_LOOKUP_NAMES[i];
      String javaName = CIPHER_NAMES_TO_JAVA_LOOKUP_NAMES[i + 1];

      // Only if OpenSSL has the cipher available, add it.
      if (availableOpenSslCiphers.contains(sslName)) {
        String javaClass =
            Cipher.class.getName() + "$" + (javaName.replace("-", "_").replace("/", "_"));
        map.put("Cipher." + javaName, javaClass);
      }
    }

    return map;
  }

  /**
   * Creates some aliases for an input map. The motivation is that the Sun provider accepts, for
   * example, "SHA1" and "SHA-1", and this library needs to stay API compatible as much as possible
   * in order to be a drop-in replacement for the Sun provider.
   *
   * @param map a map with keys being cipher names of the form "Cipher.XYZ" and the keys being Java
   *     class names.
   * @return a map mapping from cipher name / alias to cipher class.
   */
  static Map<String, String> createCipherAliases(Map<String, String> map) {
    Map<String, String> aliases = new HashMap<>();

    for (Map.Entry<String, String> entry : map.entrySet()) {
      aliases.put(entry.getKey().replace("-", ""), entry.getValue());
    }
    return aliases;
  }

  /**
   * Gets the names of all ciphers.
   *
   * @return a map mapping from cipher name to cipher class.
   */
  static Map<String, String> getCipherNames(Set<String> availableOpenSslCipherNames) {
    Map<String, String> result = getOpenSSLCiphersToImplementations(availableOpenSslCipherNames);
    result.putAll(createCipherAliases(result));
    result.put("Cipher.AES", "de.sfuhrm.openssl4j.Cipher$AES");
    result.put("Cipher.HMACSHA256", "de.sfuhrm.openssl4j.Cipher$HMACSHA256");
    result.put("Cipher.SHA256", "de.sfuhrm.openssl4j.Cipher$HMACSHA256");
    return result;
  }

  static String overlapErrorMessage(Set<String> intersection) {
    StringBuilder builder = new StringBuilder();
    builder.append("Overlap between message-digest and cipher names: ");
    boolean first = true;
    for (String overlap : intersection) {
      if (first) {
        first = false;
      } else {
        builder.append(", ");
      }
      builder.append(overlap);
    }
    return builder.toString();
  }

  static void assertNoOverlap(
      Set<String> openSslMessageDigestAlgorithms, Set<String> openSslCiphers) {
    Set<String> intersection = new HashSet<>(openSslMessageDigestAlgorithms);
    intersection.retainAll(openSslCiphers);
    if (!intersection.isEmpty()) {
      throw new IllegalStateException(overlapErrorMessage(intersection));
    }
    intersection = new HashSet<>(openSslCiphers);
    intersection.retainAll(openSslMessageDigestAlgorithms);
    if (!intersection.isEmpty()) {
      throw new IllegalStateException(overlapErrorMessage(intersection));
    }
  }

  static void dumpNames(String heading, Set<String> names) {
    System.out.println(heading + ":");
    List<String> sorted = new ArrayList<>(names);
    Collections.sort(sorted);
    for (String name : sorted) {
      System.out.println("\t" + name);
    }
  }

  static void dumpNames(String heading, Map<String, String> names) {
    System.out.println(heading + ":");
    List<String> sorted = new ArrayList<>(names.keySet());
    Collections.sort(sorted);
    for (String key : sorted) {
      System.out.println("\t" + key + " -> " + names.get(key));
    }
  }
}
