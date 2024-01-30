package de.sfuhrm.openssl4j;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * JCA provider directing all calls to the system native OpenSSL library.
 *
 * @author Stephan Fuhrmann
 */
public final class OpenSSL4JProvider extends Provider {

  /** The provider name passed to JCA. */
  public static final String PROVIDER_NAME = "OpenSSL4J";

  public static final String PROVIDER_INFO = PROVIDER_NAME + " JSP/JCE";

  private OpenSSLCryptoNative crypto = new OpenSSLCryptoNative();

  private static OpenSSL4JProvider instance = null;

  /**
   * Constructor for the JCA Provider for OpenSSL JNI.
   *
   * @throws IllegalStateException if the native object file can't be loaded and the class can't be
   *     used.
   */

   public static synchronized OpenSSL4JProvider getInstance() {
      if(OpenSSL4JProvider.instance == null) {
        OpenSSL4JProvider.instance = new OpenSSL4JProvider();
      }

      return OpenSSL4JProvider.instance;
   }

  private OpenSSL4JProvider() {
    // TODO: Get the major.middle versions from somewhere instead of hardcoding (the original
    // gets them from Maven). Unfortunately a double can't support major.middle.minor because it
    // only has one decimal point.
    super(PROVIDER_NAME, 1.0, PROVIDER_INFO);

    try {

      // Load.
      NativeLoader.loadAll();

      // Initialize sets of names if not already initialized.
      if (OpenSSL4JProviderUtils.openSslMessageDigestAlgorithms == null) {
        OpenSSL4JProviderUtils.openSslMessageDigestAlgorithms =
            OpenSSLMessageDigestNative.getMessageDigestList();
      }
      if (OpenSSL4JProviderUtils.openSslCiphers == null) {
        OpenSSL4JProviderUtils.openSslCiphers = OpenSSLCipherNative.getCipherList();
      }

      // Sanity check. This should never happen, because it would be a serious bug in OpenSSL.
      OpenSSL4JProviderUtils.assertNoOverlap(
          OpenSSL4JProviderUtils.openSslMessageDigestAlgorithms,
          OpenSSL4JProviderUtils.openSslCiphers);

      // Output debug info if enabled.
      if (OpenSSL4JProviderUtils.DEBUG) {
        OpenSSL4JProviderUtils.dumpNames(
            "MESSAGE DIGEST ALGORITHMS", OpenSSL4JProviderUtils.openSslMessageDigestAlgorithms);
        OpenSSL4JProviderUtils.dumpNames("CIPHERS", OpenSSL4JProviderUtils.openSslCiphers);
      }

      // Register names and implementations with parent Provider class.
      Map<String, String> messageDigestNames =
          OpenSSL4JProviderUtils.getMessageDigestAlgorithmNames(
              OpenSSL4JProviderUtils.openSslMessageDigestAlgorithms);
      Map<String, String> cipherNames =
          OpenSSL4JProviderUtils.getCipherNames(OpenSSL4JProviderUtils.openSslCiphers);

      // Output debug info if enabled.
      if (OpenSSL4JProviderUtils.DEBUG) {
        OpenSSL4JProviderUtils.dumpNames(
            "MESSAGE DIGEST NAMES AND IMPLEMENTATIONS", messageDigestNames);
        OpenSSL4JProviderUtils.dumpNames("CIPHER NAMES AND IMPLEMENTATIONS", cipherNames);
      }

      Map<String, String> messageDigestAndCipherNames = new HashMap<>();
      messageDigestAndCipherNames.putAll(messageDigestNames);
      messageDigestAndCipherNames.putAll(cipherNames);
      putAll(messageDigestAndCipherNames);

      String defaultSecureRandomClassName = SecureRandom.class.getName() + "$DEFAULT";

      put("SecureRandom.DEFAULT", defaultSecureRandomClassName);

      put("Mac.HMACSHA256", Mac.class.getName() + "$HMAC_SHA256");

      // putService(new Service(this, "SecureRandom", "DEFAULT", defaultSecureRandomClassName, null, null));

    } catch (IOException e) {
      throw new IllegalStateException("Could not initialize: " + e.getMessage(), e);
    }
  }

  public PrivateKey decryptPrivateKey(String privateKey, String passPhrase) {
    return crypto.decryptPrivateKey(privateKey, passPhrase);
  }
}
