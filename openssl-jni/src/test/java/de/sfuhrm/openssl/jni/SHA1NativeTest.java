package de.sfuhrm.openssl.jni;

/**
 * Test for {@linkplain SHA1Native}.
 * @author Stephan Fuhrmann
 */
public class SHA1NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected String algorithmName() {
        return "SHA1";
    }
}
