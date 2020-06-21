package de.sfuhrm.openssl.jni;

/**
 * Test for {@linkplain SHA224Native}.
 * @author Stephan Fuhrmann
 */
public class SHA224NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected String algorithmName() {
        return "SHA-224";
    }
}
