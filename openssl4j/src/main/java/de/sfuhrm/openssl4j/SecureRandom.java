package de.sfuhrm.openssl4j;

public class SecureRandom {
    private SecureRandom() {

    }

    public static final class DEFAULT extends OpenSSLSecureRandomNative {
        public DEFAULT() {
            super();
        }
    }
}
