OpenSSL JNI Java Library
===================
![Travis CI Status](https://travis-ci.org/sfuhrm/openssl-jni.svg?branch=master)
[![Javadoc](https://javadoc-badge.appspot.com/de.sfuhrm/openssl-jni.svg?label=javadoc)](http://api.sfuhrm.de/openssl-jni/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/openssl-jni/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/openssl-jni) 
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)

A Java bridge to the native C OpenSSL library. On the Java side you're
using the conventional MessageDigest class, but in the
background the nativ OpenSSL library is called with all its
optimizations for performance reasons.

## Building it

For building the application you need
* JDK 8+,
* Apache Maven,
* GNU GCC
* OpenSSL development headers
  
Use the following command line:

    $ mvn clean package

## Features

The main feature is performance: The MD5-implementation of OpenSSL-JNI is
typically 67% to 102% faster than the pure Java version from SUN.

## Usage

The following example show how to create a MD5 message digest instance:

---------------------------------------

```java
import de.sfuhrm.openssl.jni.OpenSSLProvider;

...

MessageDigest messageDigest = MessageDigest.getInstance("MD5", new OpenSSLProvider());
messageDigest.update("hello world!".getBytes(Charset.forName("ASCII")));
byte[] digest = messageDigest.digest():
```

---------------------------------------

## Including it in your projects

Please note that the current version is experimental. 

The recommended way of including the library into your project is using maven:

---------------------------------------

```xml
<dependency>
    <groupId>de.sfuhrm</groupId>
    <artifactId>openssl-jni</artifactId>
    <version>0.x.y</version>
</dependency>
```

---------------------------------------

There are the following native implementations available:
* Linux-amd64

## Versions

The version numbers are chosen according to the
[semantic versioning](https://semver.org/) schema.
Especially major version changes come with breaking API
changes.

## Author

Written 2020 by Stephan Fuhrmann. You can reach me via email to s (at) sfuhrm.de

## License

The project is licensed under [LGPL 3.0](https://www.gnu.org/licenses/lgpl-3.0.en.html).
