package de.sfuhrm.openssl4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Manual test for {@linkplain ObjectTransfer}.
 * @author Stephan Fuhrmann
 */
public class ObjectTransferTest {

    @Test
    public void enforceAlnumWithAlnum() {
        Assertions.assertEquals("foobar123", ObjectTransfer.enforceAlnum("foobar123"));
    }

    @Test
    public void enforceAlnumWithSpace() {
        Assertions.assertThrows(IllegalStateException.class, () -> ObjectTransfer.enforceAlnum("foobar 123"));
    }

    @Test
    public void enforceAlnumWithBackslash() {
        Assertions.assertThrows(IllegalStateException.class, () -> ObjectTransfer.enforceAlnum("foobar\\123"));
    }
}
