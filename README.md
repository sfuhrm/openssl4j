OpenSSL4J JNI Java Library
===================
![Travis CI Status](https://travis-ci.org/sfuhrm/openssl4j.svg?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/openssl4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/openssl4j) 
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)

OpenSSL4J is a Java bridge to the native C OpenSSL library. On the Java side you're
using the conventional MessageDigest class, but in the
background the nativ OpenSSL library is called with all its
optimizations for performance reasons.

## Building OpenSSL4J

For building the application you need
* JDK 9+,
* Apache Maven,
* GNU Make,
* GNU GCC,
* OpenSSL development headers

To build the C library, execute:

    $ make

To build the Java package, execute:

    $ mvn clean package

## Features

* Performance: The main feature of OpenSSL4J is performance: The MD5-implementation of OpenSSL4J is
typically 67% to 102% faster than the pure Java version from SUN.
* Functionality: There are some algorithms available in OpenSSL4J that are not available in the
normal SUN crypto provider.

## Restrictions

* MessageDigest restriction: The current milestone only contains MessageDigest algorithms.
* Restricted platforms: The code uses dynamic linking to an object library on the machine.
  Native object code within the JAR file is used for binding the Java code to the native code.
  There is a restricted amount of platforms supported (see below).

## Usage

### Dynamic security provider configuration

The following example show how to create a MD5 message digest instance with the
dynamically chosen security Provider:

---------------------------------------

```java
import de.sfuhrm.openssl4j.OpenSSLProvider;

...

MessageDigest messageDigest = MessageDigest.getInstance("MD5", new OpenSSLProvider());
messageDigest.update("hello world!".getBytes(Charset.forName("ASCII")));
byte[] digest = messageDigest.digest():
```

---------------------------------------

### Installing it in the JDK

You can also install the provider in your JDK installation. Open the `java.security` file in an editor:
* Linux, or macOS: `<java-home>/conf/security/java.security`
* Windows: `<java-home>\conf\security\java.security`

To be used effectively, insert it in front of the SUN provider. If this is how the original file looks

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
security.provider.1=OpenSSL
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
    <version>0.x.y</version>
</dependency>
```

---------------------------------------

There are the following native implementations available inside the JAR file:
* Linux-amd64
* Linux-arm

## Version notice

Please note that the current version is experimental. 

## Versions

The version numbers are chosen according to the
[semantic versioning](https://semver.org/) schema.
Especially major version changes come with breaking API
changes.

## Author

Written 2020 by Stephan Fuhrmann. You can reach me via email to s (at) sfuhrm.de

## License

The project is licensed under [LGPL 3.0](https://www.gnu.org/licenses/lgpl-3.0.en.html).
