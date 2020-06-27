#! /bin/bash

#
# File is executed in travisci environment
#

sudo apt-get install -y xmlstarlet || exit 10

SSL_VERSION=$(cat target/ssl-lib)
OSSL4JNAME=$(cat target/openssl4j-lib)
POM_VERSION="$(cat target/pom.version)"

curl -T ${OSSL4JNAME} \
-usfuhrm:${BINTRAY_API_KEY} \
https://api.bintray.com/content/sfuhrm/openssl4j/objects/${POM_VERSION}/libopenssl4j-$(uname -m)-$(uname -s)-${SSL_VERSION} || exit 10
