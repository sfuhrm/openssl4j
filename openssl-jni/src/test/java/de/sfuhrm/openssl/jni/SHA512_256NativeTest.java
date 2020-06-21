package de.sfuhrm.openssl.jni;

/**
 * Test for {@linkplain SHA512_256Native}.
 * @author Stephan Fuhrmann
 */
public class SHA512_256NativeTest extends AbstractMessageDigestSpiTest {

    @Override
    protected String algorithmName() {
        return "SHA-512/256";
    }
}
