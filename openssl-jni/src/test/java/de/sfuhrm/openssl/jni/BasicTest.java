package de.sfuhrm.openssl.jni;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Test base.
 * @author Stephan Fuhrmann
 */
public abstract class BasicTest {

    Formatter formatter;
    Charset ascii;

    @BeforeEach
    public void before() throws IOException {
        NativeLoader.loadAll();

        formatter = Formatter.getInstance();
        ascii = Charset.forName("ASCII");
    }
}
