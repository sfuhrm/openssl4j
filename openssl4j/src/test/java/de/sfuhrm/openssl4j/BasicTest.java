package de.sfuhrm.openssl4j;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.charset.Charset;

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
