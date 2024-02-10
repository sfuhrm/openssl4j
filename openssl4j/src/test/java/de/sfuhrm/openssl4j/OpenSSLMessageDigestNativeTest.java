package de.sfuhrm.openssl4j;

import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for {@linkplain OpenSSLMessageDigestNative}.
 *
 * @author Stephan Fuhrmann
 */
public class OpenSSLMessageDigestNativeTest {

    @BeforeEach
    public void init() throws IOException {
        NativeLoader.loadAll();
    }

    @Test
    public void getMessageDigestList() {
        Set<String> sslAlgos = OpenSSLMessageDigestNative.getMessageDigestList();
        Assertions.assertNotNull(sslAlgos);
        Assertions.assertNotEquals(0, sslAlgos.size());
        Assertions.assertTrue(sslAlgos.contains("MD5"));
    }

    @Test
    public void freeWithNull() {
        Assertions.assertThrows(NullPointerException.class, () -> OpenSSLMessageDigestNative.free(null));
    }
}
