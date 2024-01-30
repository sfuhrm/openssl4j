OpenSSL4J JNI Java Library
===================
![Travis CI Status](https://travis-ci.org/sfuhrm/openssl4j.svg?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/openssl4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/openssl4j)
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)

OpenSSL4J is a Java bridge to the native OpenSSL library. On the Java side you're
using the conventional MessageDigest class, but this library calls in the
background the native OpenSSL library with all its
optimizations for performance reasons.

## Building OpenSSL4J

For building the application you need

* JDK 8,
* Apache Maven,
* GNU Make,
* GNU GCC,
* OpenSSL development headers

To build the C library and install it to the right place in `openssl4j/src/main/resources/objects`, execute:

    $ make

To build the Java package, execute:

    $ mvn clean package

## Features

* Performance: The main feature of OpenSSL4J is performance: The MD5-implementation of OpenSSL4J is
  typically 67% to 102% faster than the pure Java version from SUN.
* Functionality: There are some algorithms available in OpenSSL4J that are not available in the
  normal SUN crypto provider.
* FIPS: When compiled against OpenSSL FIPS
  (https://csrc.nist.gov/CSRC/media/projects/cryptographic-module-validation-program/documents/security-policies/140sp4271.pdf,
  https://csrc.nist.gov/CSRC/media/projects/cryptographic-module-validation-program/documents/security-policies/140sp4282.pdf),
  this library delivers FIPS-140-2 compliance. This is preferable to using bc-fips in combination with bouncy-castle,
  because this library doesn't know or care at all about Java, specific Java versions, or classpath ordering. You can
  use any version of Java you want, with whatever classpath you desire. And, unlike with bc-fips, you don't have to wait
  years for the next Java version to be supported.

## Restrictions

* This library is not a full wrapper over Open SSL. All message digests are supported, but only those ciphers,
  algorithms, HMAC, and SecureRandom needed for FIPS are currently supported. However, there is no technical reason full
  Open SSL support can't be added, and we welcome contributions.

## Usage

### Dynamic security provider configuration

The following example show how to create a MD5 message digest instance with the
dynamically chosen security Provider:

---------------------------------------

```java
import de.sfuhrm.openssl4j.OpenSSL4JProvider;

...

  MessageDigest messageDigest = MessageDigest.getInstance("MD5", OpenSSL4JProvider.getInstance());
  messageDigest.update("hello world!".getBytes(Charset.forName("ASCII")));
  byte[]digest=messageDigest.digest():
```

---------------------------------------

### Installing it in the JDK

You can also install the provider in your JDK installation. Open the `java.security` file in an editor:

* Linux, or macOS: `<java-home>/conf/security/java.security`
* Windows: `<java-home>\conf\security\java.security`

To be used effectively, insert it in front of the SUN provider. If this is how the original file looks:

---------------------------------------

```
security.provider.1=SUN
security.provider.2=SunRsaSign
security.provider.3=SunEC
security.provider.4=SunJSSE
security.provider.5=SunJCE
security.provider.6=SunJGSS
security.provider.7=SunSASL
security.provider.8=XMLDSig
security.provider.9=SunPCSC
...
```

---------------------------------------

then the new file could look like this after inserting and renumbering the entries:

---------------------------------------

```
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
...
```

---------------------------------------

## Including it with Maven

The recommended way of including the library into your project is using maven:

---------------------------------------

```xml

<dependency>
  <groupId>de.sfuhrm</groupId>
  <artifactId>openssl4j</artifactId>
  <version>0.2.0</version>
</dependency>
```

---------------------------------------

There are the following native implementations available inside the JAR file:

* Linux-aarch64
* Linux-amd64
* Linux-arm
* Linux-ppc64le
* Linux-s390x

## Version notice

Please note that the current version is experimental.

## Versions

The version numbers comply to the
[semantic versioning](https://semver.org/) schema.
Especially major version changes come with breaking API
changes.

## Author

Written 2020-2022 by Stephan Fuhrmann. You can reach me via email to s (at) sfuhrm.de

## License

The project is licensed under [LGPL 3.0](https://www.gnu.org/licenses/lgpl-3.0.en.html).
