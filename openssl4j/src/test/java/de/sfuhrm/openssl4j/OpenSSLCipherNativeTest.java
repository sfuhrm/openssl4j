package de.sfuhrm.openssl4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

/**
 * Test for {@linkplain OpenSSLCipherNative}.
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
    public void newInstance() {
        OpenSSLCipherNative openSSLCipherNative = new OpenSSLCipherNative("AES-256-OCB");
        Assertions.assertNotNull(openSSLCipherNative);
    }
}
