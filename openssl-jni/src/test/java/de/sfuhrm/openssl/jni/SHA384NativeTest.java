package de.sfuhrm.openssl.jni;

/**
 * Test for {@linkplain SHA384Native}.
 * @author Stephan Fuhrmann
 */
public class SHA384NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected String algorithmName() {
        return "SHA-384";
    }
}
