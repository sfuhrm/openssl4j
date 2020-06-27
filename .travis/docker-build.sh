#! /bin/bash

#
# File is executed in docker environment
#
# Dirs:
# /build - the mounted git repo
# /jdk - a link to AdoptOpenJDK 11
# /maven - a Apache maven build tool
#

source /etc/profile.d/java_home.sh
source /etc/profile.d/maven.sh

cd /build     || exit 10

mvn clean     || exit 10
make clean    || exit 10
make all      || exit 10
ls -al target || exit 10
make install  || exit 10

# create file "ssl-lib" saying which dynamic ssl so lib is needed
ldd target/libopenssl4j-* | sed -n 's/^.*\(libssl.so.[0-9.]*\).*/\1/p' > target/ssl-lib   || exit 10
(cd target && echo libopenssl4j*) > target/openssl4j-lib   || exit 10

mvn package   || exit 10
