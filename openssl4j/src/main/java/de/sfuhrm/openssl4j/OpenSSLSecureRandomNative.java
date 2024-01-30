package de.sfuhrm.openssl4j;

import java.security.SecureRandomSpi;

public class OpenSSLSecureRandomNative extends SecureRandomSpi {

  private static native void engineSetSeedNative(byte[] seed, int seedLen, double randomness);
  private static native int engineNextBytesNative(byte[] rand, int randLen);

  @Override
  protected void engineSetSeed(byte[] seed) {
    engineSetSeedNative(seed, seed.length, seed.length);
  }

  @Override
  protected void engineNextBytes(byte[] bytes) {
    engineNextBytesNative(bytes, bytes.length);
  }

  @Override
  protected byte[] engineGenerateSeed(int numBytes) {
    byte[] seed = new byte[numBytes];

    engineNextBytesNative(seed, numBytes);

    return seed;
  }
}
