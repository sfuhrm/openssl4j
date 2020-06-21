package de.sfuhrm.openssl.jni;

/**
 * Test for {@linkplain SHA3_384Native}.
 * @author Stephan Fuhrmann
 */
public class SHA3_384NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected String algorithmName() {
        return "SHA3-384";
    }
}
