#! /bin/bash

# Travis vars: https://docs.travis-ci.com/user/environment-variables/

echo "\$TRAVIS_OS_NAME: $TRAVIS_OS_NAME"
echo "\$TRAVIS_DIST: $TRAVIS_DIST"
echo "\$TRAVIS_CPU_ARCH: $TRAVIS_CPU_ARCH"
echo "\$JAVA_HOME: $JAVA_HOME"

echo "JAVA location: " $(which java)
echo "JAVAC location: " $(which javac)

# first check some basics
javac -version || exit 5
make clean || exit 10

if [ "x" = "x$JAVA_HOME" ]; then
  echo "No JAVA_HOME set"
  exit 10
fi

# build
make || exit 10

# curl -T target/libopenssl4j* -usfuhrm:${BINTRAY_API_KEY} https://api.bintray.com/content/sfuhrm/openssl4j/objects/0.0.0/libopenssl4j-$(uname -m)-$(uname -s)-$(uname -r)
