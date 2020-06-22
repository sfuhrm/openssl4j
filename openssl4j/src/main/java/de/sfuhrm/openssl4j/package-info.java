/**
 * Binding classes from the Java JCA providers. The
 * native OpenSSL library is being linked dynamically
 * into the JVMs address space.
 *
 * <h3>Usage</h3>
 * There are multiple ways of using the library.
 *
 * <h4>Runtime configuration</h4>
 * {@code MessageDigest md5 = new MessageDigest("MD5", new OpenSSLProvider());}
 *
 * <h4>Runtime configuration</h4>
 * {@code MessageDigest md5 = new MessageDigest("MD5", new OpenSSLProvider());}
 */
package de.sfuhrm.openssl4j;