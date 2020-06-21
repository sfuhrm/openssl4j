package de.sfuhrm.openssl.jni;

/**
 * Test for {@linkplain SHA3_224Native}.
 * @author Stephan Fuhrmann
 */
public class SHA3_224NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected String algorithmName() {
        return "SHA3-224";
    }
}