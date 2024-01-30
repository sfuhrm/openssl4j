package de.sfuhrm.openssl4j;

public class OpenSSL4JException extends RuntimeException {

  public OpenSSL4JException(final String msg) {
    super(msg);
  }

  public OpenSSL4JException(final String msg, Throwable t) {
    super(msg, t);
  }
}
