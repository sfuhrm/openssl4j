package de.sfuhrm.openssl4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for {@linkplain OpenSSLCipherNative}.
 *
 * @author Stephan Fuhrmann
 */
public class OpenSSLCipherNativeTest {

  @BeforeEach
  public void init() throws IOException {
    NativeLoader.loadAll();
  }

  @Test
  public void getCipherList() {
    Set<String> sslAlgos = OpenSSLCipherNative.getCipherList();
    Assertions.assertNotNull(sslAlgos);
    Assertions.assertNotEquals(0, sslAlgos.size());
    Assertions.assertTrue(sslAlgos.contains("AES-256-OCB"));
  }

  @Test
  public void newInstance() throws IOException {
    String LINE_SEPARATOR = System.getProperty("line.separator");
    try {
      OpenSSLCipherNative openSSLCipherNative = new OpenSSLCipherNative("AES-256-OCB");
      Assertions.assertNotNull(openSSLCipherNative);
    } catch (Exception e) {
      StringBuilder builder = new StringBuilder();
      builder.append("ERROR CREATING OPEN SSL CIPHER NATIVE: ");
      builder.append(e.getMessage());
      for (StackTraceElement stackTraceElement : e.getStackTrace()) {
        builder.append(LINE_SEPARATOR);
        builder.append(stackTraceElement.toString());
      }
      BufferedWriter writer =
          new BufferedWriter(new FileWriter("OpenSSLCipherNative_ERROR.txt", true));
      writer.append(builder.toString());
      writer.close();
      throw new OpenSSL4JException(builder.toString());
    }
  }
}
