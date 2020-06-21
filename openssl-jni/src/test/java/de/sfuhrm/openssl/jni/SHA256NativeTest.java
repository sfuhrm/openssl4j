package de.sfuhrm.openssl.jni;

/**
 * Test for {@linkplain SHA256Native}.
 * @author Stephan Fuhrmann
 */
public class SHA256NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected String algorithmName() {
        return "SHA-256";
    }
}
