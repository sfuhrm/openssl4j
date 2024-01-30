package de.sfuhrm.openssl4j;

import de.sfuhrm.openssl4j.*;


public class Cipher {
  private Cipher() {

  }

  public static final class AES extends OpenSSLCipherNative {
    public AES() {
      super("AES-256");
    }
  }

  public static final class SHA512 extends OpenSSLCipherNative {
    public SHA512() {
      super("SHA512");
    }
  }

  public static final class SHA256 extends OpenSSLCipherNative {
    public SHA256() {
      super("SHA256");
    }
  }

  public static final class SHA1 extends OpenSSLCipherNative{
    public SHA1() {
      super("SHA1");
    }
  }

  public static final class AES_256_GCM extends OpenSSLCipherNative{
    public AES_256_GCM() {
      super("AES-256-GCM");
    }
  }

  public static final class AES_256_CBC extends OpenSSLCipherNative{
    public AES_256_CBC() {
      super("AES-256-CBC");
    }
  }

  public static final class AES_256_CTR extends OpenSSLCipherNative{
    public AES_256_CTR() {
      super("AES_256_CTR");
    }
  }

  public static final class AES_256_ECB extends OpenSSLCipherNative{
    public AES_256_ECB() {
      super("AES_256_ECB");
    }
  }

  public static final class id_aes256_GCM extends OpenSSLCipherNative{
    public id_aes256_GCM() { 
      super("AES_256_GCM");
    }
  }

  public static final class HMACSHA256 extends OpenSSLCipherNative {
    public HMACSHA256() {
      super("HMAC_SHA_256");
    }
  }
}
