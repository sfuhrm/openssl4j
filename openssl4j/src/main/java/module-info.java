import de.sfuhrm.openssl4j.OpenSSLProvider;

module de.sfuhrm.openssl4j {
    provides java.security.Provider with OpenSSLProvider;
    exports de.sfuhrm.openssl4j to java.base;
}
