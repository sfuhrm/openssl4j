import de.sfuhrm.openssl4j.OpenSSL4JProvider;

module de.sfuhrm.openssl4j {
    provides java.security.Provider with OpenSSL4JProvider;
    exports de.sfuhrm.openssl4j to java.base;
}
