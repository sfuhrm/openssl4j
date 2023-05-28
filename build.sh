#!/bin/bash

#
# Build script for building a jar with just one platform!
#
# The full multi-platform build is working in combination with
# QEMU on github actions.
#

MY_VERSION=0-SNAPSHOT

echo "openssl4j-objects artifact version: $MY_VERSION"
# build c library
make clean install
# build pom wrapper openssl4j-objects
(cd openssl4j-objects && \
 mvn versions:set -DnewVersion=$MY_VERSION && \
 mvn install )
# build jar using openssl4j-objects
mvn -Dopenssl4j-objects.version=$MY_VERSION clean package
