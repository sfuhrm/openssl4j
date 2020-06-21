package de.sfuhrm.openssl.jni;

/**
 * Test for {@linkplain SHA3_256Native}.
 * @author Stephan Fuhrmann
 */
public class SHA3_256NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected String algorithmName() {
        return "SHA3-256";
    }
}
