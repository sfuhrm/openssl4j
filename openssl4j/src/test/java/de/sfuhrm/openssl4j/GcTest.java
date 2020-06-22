package de.sfuhrm.openssl4j;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Manual test for {@linkplain PhantomReferenceCleanup}.
 * @author Stephan Fuhrmann
 */
public class GcTest {

    @Test
    @Disabled
    public void gc() {
        for (int i=0; i< 10000000; i++) {
            OpenSSL.MD5 md5Native = new OpenSSL.MD5();
            System.gc();
        }
    }
}
