package de.sfuhrm.openssl4j;

public class Mac {
    private Mac() {

    }

    public static final class HMAC_SHA256 extends OpenSSLMacNative {
        public HMAC_SHA256() {
            super("sha256");
        }
    }
}
