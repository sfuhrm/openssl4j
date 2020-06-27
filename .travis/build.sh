#! /bin/bash

#
# File is executed in docker environment
#
# Dirs:
# /build - the mounted git repo
# /jdk - a link to AdoptOpenJDK 11
#

source /etc/profile.d/java_home.sh
cd /build
make clean
make all
ls -al target

# curl -T target/libopenssl4j* -usfuhrm:${BINTRAY_API_KEY} https://api.bintray.com/content/sfuhrm/openssl4j/objects/0.0.0/libopenssl4j-$(uname -m)-$(uname -s)-$(uname -r)
