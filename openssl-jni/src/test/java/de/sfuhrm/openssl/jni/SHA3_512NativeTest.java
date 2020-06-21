package de.sfuhrm.openssl.jni;

/**
 * Test for {@linkplain SHA3_512Native}.
 * @author Stephan Fuhrmann
 */
public class SHA3_512NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected String algorithmName() {
        return "SHA3-512";
    }
}
