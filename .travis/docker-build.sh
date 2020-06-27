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

cd /build

mvn clean
make clean
make all
ls -al target

# create file "ssl-lib" saying which dynamic ssl so lib is needed
ldd target/libopenssl4j-* | sed -n 's/^.*\(libssl.so.[0-9.]*\).*/\1/p' > target/ssl-lib
echo target/libopenssl4j* > target/openssl4j-lib

mvn package
