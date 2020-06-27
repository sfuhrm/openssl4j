#! /bin/bash

#
# File is executed in travisci environment
#

SSL_VERSION=$(cat target/ssl-lib)
OSSL4JNAME=$(cat target/openssl4j-lib)
POM_VERSION="$(cat target/pom.version)"

POM_VERSION_BASE="$(echo $POM_VERSION | cut -d"-" -f1)"
POM_VERSION_ADD="$(echo $POM_VERSION | cut -d"-" -f2)"
echo POM_VERSION_BASE=${POM_VERSION_BASE}
echo POM_VERSION_ADD=${POM_VERSION_ADD}

if [ "${POM_VERSION_ADD}" = "SNAPSHOT" ]; then
  BINTRAY_PACKAGE=objects-snapshot
else
  BINTRAY_PACKAGE=objects-release
fi
echo BINTRAY_PACKAGE=${BINTRAY_PACKAGE}

for FILE in ${OSSL4JNAME} ${OSSL4JNAME}.txt; do
  echo ${FILE}
  curl -T target/${FILE} \
  -usfuhrm:${BINTRAY_API_KEY} \
  https://api.bintray.com/content/sfuhrm/openssl4j/${BINTRAY_PACKAGE}/${POM_VERSION_BASE}/${TRAVIS_COMMIT}/$(uname -m)-$(uname -s)-${SSL_VERSION}/${FILE} || exit 10

  curl -X DELETE \
  -usfuhrm:${BINTRAY_API_KEY} \
  https://api.bintray.com/content/sfuhrm/openssl4j/${BINTRAY_PACKAGE}/${POM_VERSION_BASE}/latest/$(uname -m)-$(uname -s)-${SSL_VERSION}/${FILE}

  curl -T target/${FILE} \
  -usfuhrm:${BINTRAY_API_KEY} \
  https://api.bintray.com/content/sfuhrm/openssl4j/${BINTRAY_PACKAGE}/${POM_VERSION_BASE}/latest/$(uname -m)-$(uname -s)-${SSL_VERSION}/${FILE} || exit 10
done
