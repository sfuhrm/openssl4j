OpenSSL4J JNI Java Library
===================
[![Java Build](https://github.com/sfuhrm/openssl4j/actions/workflows/build-java.yml/badge.svg)](https://github.com/sfuhrm/openssl4j/actions/workflows/build-java.yml)
[![Crossplatform Build](https://github.com/sfuhrm/openssl4j/actions/workflows/build-crossplatform.yml/badge.svg)](https://github.com/sfuhrm/openssl4j/actions/workflows/build-crossplatform.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/openssl4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/openssl4j) 
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)

OpenSSL4J is a Java bridge to the native OpenSSL library. On the Java side you're
using the conventional MessageDigest class, but calls in the
background the native OpenSSL library with all its
optimizations for performance reasons.

## Building OpenSSL4J

For building the application you need
* JDK 9+,
* Apache Maven,
* GNU Make,
* GNU GCC,
* OpenSSL development headers

To build the C library, wrap it into a maven artifact (openssl4j-objects),
build the java parts (openssl4j), execute:

```bash
$ build.sh
...
[INFO] Reactor Summary for OpenSSL4J Parent 0.2.1-SNAPSHOT:
[INFO] 
[INFO] OpenSSL4J Parent ................................... SUCCESS [  0.953 s]
[INFO] OpenSSL4J JNI ...................................... SUCCESS [  5.859 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  6.912 s
[INFO] Finished at: 2023-05-28T20:38:43+02:00
[INFO] ------------------------------------------------------------------------    
```

## Features

* Performance: The main feature of OpenSSL4J is performance: The MD5-implementation of OpenSSL4J is
typically 67% to 102% faster than the pure Java version from SUN.
* Functionality: There are some algorithms available in OpenSSL4J that are not available in the normal SUN crypto provider.

## Restrictions

* MessageDigest restriction: The current milestone only contains MessageDigest algorithms.
* Restricted platforms: The code uses dynamic linking to an object library on the machine.
  Native object code within the JAR file is used for binding the Java code to the native code.
  There is a restricted amount of platforms supported by the Github Actions
  builder (see below).

## Usage

### Dynamic security provider configuration

The following example show how to create a MD5 message digest instance with the
dynamically chosen security Provider:

---------------------------------------

```java
import de.sfuhrm.openssl4j.OpenSSL4JProvider;

...

MessageDigest messageDigest = MessageDigest.getInstance("MD5", new OpenSSL4JProvider());
messageDigest.update("hello world!".getBytes(Charset.forName("ASCII")));
byte[] digest = messageDigest.digest():
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

The version numbers used by `openssl4j` itself comply to the
[semantic versioning](https://semver.org/) schema.
Especially major version changes come with breaking API
changes.

The temporary internal `openssl4j-objects` artifact is using
date-derived versions, but it is invisible to maven users.

## Author

Written 2020-2023 by Stephan Fuhrmann. You can reach me via email to s (at) sfuhrm.de

## License

The project is licensed under [LGPL 3.0](https://www.gnu.org/licenses/lgpl-3.0.en.html).
