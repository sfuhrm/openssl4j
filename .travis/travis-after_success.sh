#! /bin/bash

#
# File is executed in travisci environment
#

sudo apt-get install -y xmlstarlet || exit 10

POM_VERSION="$(xmlstarlet sel -N p="http://maven.apache.org/POM/4.0.0" -t -v "/p:project/p:version/text()" pom.xml)"
SSL_VERSION=$(cat target/ssl-lib)
OSSL4JNAME=$(cat target/openssl4j-lib)

curl -T ${OSSL4JNAME} \
-usfuhrm:${BINTRAY_API_KEY} \
https://api.bintray.com/content/sfuhrm/openssl4j/objects/${POM_VERSION}/libopenssl4j-$(uname -m)-$(uname -s)-${SSL_VERSION} || exit 10
