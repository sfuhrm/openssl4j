package de.sfuhrm.openssl.jni;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.MessageDigestSpi;
import java.security.NoSuchAlgorithmException;

/**
 * Test for {@linkplain MD5Native}.
 * @author Stephan Fuhrmann
 */
public class MD5NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected MessageDigestSpi newTestMD() {
        return new MD5Native();
    }

    @Override
    protected MessageDigest newReferenceMD() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5");
    }
}
