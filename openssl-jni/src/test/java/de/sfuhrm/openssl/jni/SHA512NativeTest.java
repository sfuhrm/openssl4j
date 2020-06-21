package de.sfuhrm.openssl.jni;

/**
 * Test for {@linkplain SHA512Native}.
 * @author Stephan Fuhrmann
 */
public class SHA512NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected String algorithmName() {
        return "SHA-512";
    }
}
