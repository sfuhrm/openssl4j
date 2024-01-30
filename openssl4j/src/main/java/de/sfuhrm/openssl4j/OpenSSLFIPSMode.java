package de.sfuhrm.openssl4j;

public class OpenSSLFIPSMode {

  public static boolean isReady() {
    try {
      long handle = OpenSSLCryptoNative.getLibInstance();
      return OpenSSLCryptoNative.FIPSMode(handle) == 1;
    } catch (Exception e) {
      throw new OpenSSL4JException("Unable to get FIPS isReady: " + e.getMessage(), e);
    }
  }
}
