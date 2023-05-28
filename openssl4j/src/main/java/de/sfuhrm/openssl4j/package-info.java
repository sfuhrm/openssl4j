/**
 * Binding classes from the Java JCA providers. The
 * native OpenSSL library is being linked dynamically
 * into the JVMs address space.
 *
 * <h2>Usage</h2>
 * There are multiple ways of using the library.
 *
 * <h3>Runtime configuration</h3>
 *
 * You can create an instance of a message digest as follows if you are
 * explicitly specifying the crypto provider {@code OpenSSL4JProvider}:
 *
 * {@code MessageDigest md5 = new MessageDigest("MD5", new OpenSSL4JProvider());}
 *
 * <h3>JDK-wide configuration</h3>
 *
 * You can specify the {@code OpenSSL4JProvider} to be used JDK-wide
 * implicitly by the by changing your
 * <ul>
 * <li>Linux, or macOS: `&lt;java-home&gt;/conf/security/java.security</li>
 * <li>Windows: `&lt;java-home&gt;\conf\security\java.security</li>
 *</ul>
 *
 * to have the OpenSSL4J provider in the first place:
 *
 * <code>
 security.provider.1=OpenSSL4J
 security.provider.2=SUN
 security.provider.3=SunRsaSign
 security.provider.4=SunEC
 security.provider.5=SunJSSE
 security.provider.6=SunJCE
 security.provider.7=SunJGSS
 security.provider.8=SunSASL
 security.provider.9=XMLDSig
 security.provider.10=SunPCSC
 </code>
 */
package de.sfuhrm.openssl4j;