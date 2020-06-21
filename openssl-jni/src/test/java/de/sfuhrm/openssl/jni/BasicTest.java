package de.sfuhrm.openssl.jni;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.MessageDigestSpi;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    protected MessageDigestSpiAdapter newTestAdapter() throws NoSuchMethodException, NoSuchAlgorithmException, IOException {
        return new MessageDigestSpiAdapter(newTestMD());
    }

    protected abstract MessageDigestSpi newTestMD() throws NoSuchAlgorithmException, IOException;
    protected abstract MessageDigest newReferenceMD() throws NoSuchAlgorithmException;

}
