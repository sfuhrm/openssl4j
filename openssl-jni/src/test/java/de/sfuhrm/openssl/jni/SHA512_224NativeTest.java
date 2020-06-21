package de.sfuhrm.openssl.jni;

/**
 * Test for {@linkplain SHA512_224Native}.
 * @author Stephan Fuhrmann
 */
public class SHA512_224NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected String algorithmName() {
        return "SHA-512/224";
    }
}