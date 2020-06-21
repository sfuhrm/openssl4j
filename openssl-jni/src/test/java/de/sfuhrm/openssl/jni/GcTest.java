package de.sfuhrm.openssl.jni;

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
            MD5Native md5Native = new MD5Native();
            md5Native.digestLength();
            System.gc();
        }
    }
}
