import de.sfuhrm.openssl4j.OpenSSL4JProvider;

/**
 * Provides the OpenSSL4j cryptographic provider implementation
 * for use with the native OpenSSL dynamic library.
 */
module de.sfuhrm.openssl4j {
    provides java.security.Provider with OpenSSL4JProvider;
    exports de.sfuhrm.openssl4j to java.base;
}
