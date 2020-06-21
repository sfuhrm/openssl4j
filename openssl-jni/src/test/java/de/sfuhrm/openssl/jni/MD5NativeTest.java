package de.sfuhrm.openssl.jni;

/**
 * Test for {@linkplain MD5Native}.
 * @author Stephan Fuhrmann
 */
public class MD5NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected String algorithmName() {
        return "MD5";
    }
}
