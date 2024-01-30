package de.sfuhrm.openssl4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class OpenSSLCryptoNative {

  public static native int FIPSMode(long handle);

  public static native long CreateOpenSSLLibNative(int fips, String config, String libDir);
  public static native byte[] decryptPrivateKeyNative(String privateKey, String passPhrase);

  private static long libHandle = -1;
  private static boolean configsUnpacked = true;

  private static String openssl4JBasePath = "/openssl4j";

  static String[] ConfFilesToUnpack = {"openssl.cnf"};

  public static Path getConfigDir() {
    if(System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("mac")) {
      return  Paths.get("/", "etc", "openssl3");
    }else {
      return  Paths.get("/", "etc", "ssl", "ossl3");
    }
  }

  public static String getConfigResourceName(String fileName) {
    if(System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("mac")) {
      if(ObjectTransfer.getArchName().equals("aarch64")) {
        return  "mac-config/arm/" + fileName;
      }
      return  "mac-config/x86/" + fileName;

    }else {
      return  "rhel/x86/" + fileName;
    }
  }

  static synchronized void installOpenSSLConfigs() {
    if(configsUnpacked) {
      return;
    }

    if(!(System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("mac"))){
      // OS is not Mac, so the conf files should be on the disk.
      configsUnpacked = true;
      return;
    }

    Path exportDir = getConfigDir();
    exportDir.toFile().mkdirs();

    for(int i = 0; i < ConfFilesToUnpack.length; i++) {
      String libName = ConfFilesToUnpack[i];

      try {
        InputStream inputStream = OpenSSLCryptoNative.class.getClassLoader().getResourceAsStream(getConfigResourceName(libName));
        
        if(inputStream == null) {
          throw new RuntimeException("Failed to load openssl conf resource stream. Stream is null. Path: " + getConfigResourceName(libName));
        }

        if(exportDir.resolve(libName).toFile().exists()) {
          boolean match = true;

          InputStream existingFile = new FileInputStream(exportDir.resolve(libName).toFile());

          for(int lhs = inputStream.read(), rhs = existingFile.read(); inputStream.available() > 0 && existingFile.available() > 0; lhs = inputStream.read(), rhs = existingFile.read()) {
            if(lhs != rhs) {
              break;
            }
          }

          if(inputStream.available() > 0 || existingFile.available() > 0) {
            match = false;
          }

          if(match) {
            continue;
          }else {
            existingFile.close();
            exportDir.resolve(libName).toFile().delete();
            inputStream.close();
            inputStream = OpenSSLCryptoNative.class.getClassLoader().getResourceAsStream(libName); // Reset stream.
          }
        }

        Files.createDirectories(exportDir);
        Files.copy(inputStream, exportDir.resolve(libName));

      }catch(Exception ex) {
        System.err.println("Failed to load resource stream");
        throw new RuntimeException(ex);
      }
    }

    configsUnpacked = true;
  }

  public static synchronized long getLibInstance() {
    installOpenSSLConfigs();
    Path confFile = getConfigDir().resolve("openssl.cnf");

    System.out.flush();
    if (libHandle == -1) {
      Path libDirPath = NativeLoader.getLibraryDir();
      String libDirString = "";

      if(System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("mac")) {
        libDirString =  Paths.get(openssl4JBasePath, "/OpenSSL/lib/ossl-modules/").toString();
      }else {
        libDirString = "/usr/lib64/ossl-modules/";
      }

      libHandle = CreateOpenSSLLibNative(1, confFile.toString(), libDirString);
    }

    return libHandle;
  }

  PrivateKey decryptPrivateKey(String privateKey, String passPhrase) {

    byte[] pKeyBytes = decryptPrivateKeyNative(privateKey, passPhrase);

    if(pKeyBytes == null || pKeyBytes.length == 0) {
      throw new RuntimeException("decryptPrivateKeyNative returned a null byte array");
    }

    String pKeyStr = new String(pKeyBytes);
    pKeyStr = pKeyStr.replace("-----BEGIN PRIVATE KEY-----", "").replaceAll(System.lineSeparator(), "");
    pKeyStr = pKeyStr.replace("-----END PRIVATE KEY-----", "");

    byte[] b = Base64.getMimeDecoder().decode(pKeyStr);

    try {      
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(b);
      PrivateKey privKey = kf.generatePrivate(keySpec);
      return privKey;
    }catch (Exception ex) {
      System.out.println("Error decrypting private key:");
      System.out.println(ex);
    }
    return null;
  }
}
