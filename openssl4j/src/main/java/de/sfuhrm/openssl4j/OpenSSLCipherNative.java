package de.sfuhrm.openssl4j;

import javax.crypto.CipherSpi;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigestSpi;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An interface to OpenSSL cipher functions.
 * @author Stephan Fuhrmann
 */
class OpenSSLCipherNative /* extends CipherSpi */ {

    /** Get the list of Cipher algorithms supported by OpenSSL.
     * @return  an array of supported cipher algorithms from the OpenSSL library.
     * */
    private native static String[] listCiphers();

    /** Get the list of digest algorithms supported by the OpenSSL library.
     * @return a Set of supported message digest algorithms.
     *  */
    protected static Set<String> getCipherList() {
        String[] messageDigestAlgorithms = listCiphers();
        return new HashSet<>(Arrays.asList(messageDigestAlgorithms));
    }
}
