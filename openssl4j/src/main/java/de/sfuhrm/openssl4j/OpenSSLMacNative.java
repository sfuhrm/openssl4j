package de.sfuhrm.openssl4j;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;

import javax.crypto.MacSpi;

import de.sfuhrm.openssl4j.OpenSSLCryptoNative;

public class OpenSSLMacNative extends MacSpi {

  String curAlgorithim;
  Key curKey;
  ArrayList<Byte> buf = new ArrayList<Byte>();

  private static native int getMaxMacLength();
  private static native int hmacNative(long libCtx, String algName, byte[] key, int keyLen, byte[] data, int dataLen, byte[] output);


  OpenSSLMacNative(String algorithim) {
    curAlgorithim = algorithim;
  }

  @Override
  protected int engineGetMacLength() {
    return getMaxMacLength();
  }

  @Override
  protected void engineInit(Key key, AlgorithmParameterSpec params)
      throws InvalidKeyException, InvalidAlgorithmParameterException {
    curKey = key;
  }

  @Override
  protected void engineUpdate(byte input) {
    buf.add(input);
  }

  @Override
  protected void engineUpdate(byte[] input, int offset, int len) {
    for(int i = offset; i < len && i < input.length; i++) {
      buf.add(input[i]);
    }
  }

  @Override
  protected byte[] engineDoFinal() {
    byte[] resp = new byte[engineGetMacLength()];
    byte[] encodedKey = curKey.getEncoded();

    byte[] buffer = new byte[buf.size()];

    for(int i = 0; i < buf.size(); i++) {
      buffer[i] = buf.get(i);
    }

    int respSize = hmacNative(OpenSSLCryptoNative.getLibInstance(), curAlgorithim, encodedKey, encodedKey.length, buffer, buffer.length, resp);

    if(respSize < resp.length) {
      byte[] result = new byte[respSize];

      for(int i = 0 ; i < respSize; i++) {
        result[i] = resp[i];
      }

      return result;
    }


    return resp;
  }

  @Override
  protected void engineReset() {
    buf.clear();
  }
}
