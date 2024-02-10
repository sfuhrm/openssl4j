package de.sfuhrm.openssl4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Manual test for {@linkplain ObjectTransfer}.
 *
 * @author Stephan Fuhrmann
 */
public class ObjectTransferTest {

    @Test
    public void enforceAlnumWithAlnum() {
        System.setProperty("foo", "foobar123");
        Assertions.assertEquals("foobar123", ObjectTransfer.getSystemPropertyAlnum("foo"));
    }

    @Test
    public void enforceAlnumWithSpace() {
        System.setProperty("foo", "foobar 123");
        Assertions.assertEquals("foobar_123", ObjectTransfer.getSystemPropertyAlnum("foo"));
    }

    @Test
    public void enforceAlnumWithBackslash() {
        System.setProperty("foo", "foobar\\123");
        Assertions.assertEquals("foobar_123", ObjectTransfer.getSystemPropertyAlnum("foo"));
    }

    @Test
    public void enforceAlnumWithNotExist() {
        Assertions.assertThrows(NullPointerException.class, () -> ObjectTransfer.getSystemPropertyAlnum("NOT_EXIST"));
    }

    @Test
    public void enforceAlnumWithNull() {
        Assertions.assertThrows(NullPointerException.class, () -> ObjectTransfer.getSystemPropertyAlnum(null));
    }
}
