#! /bin/bash
#
# Downloads the TravisCI-built library files to the local path
# that will be integrated in the built Maven artifact.

for OS in Linux; do
  for ARCH in "aarch64 aarch64" "ppc64le ppc64le" "s390x s390x" "x86_64 amd64"; do
    for SSLVERSION in libssl.so.1.1; do
      # https://dl.bintray.com/sfuhrm/openssl4j/latest/aarch64-Linux-libssl.so.1.1/libopenssl4j-Linux-aarch64.so.txt
      ARCH_LINUX=$(echo ${ARCH} | cut -d" " -f1)
      ARCH_JAVA=$(echo ${ARCH} | cut -d" " -f2)
      FILE=libopenssl4j-${OS}-${ARCH_JAVA}.so
      DIR=${ARCH_LINUX}-${OS}-${SSLVERSION}
      echo ${FILE}
      curl https://dl.bintray.com/sfuhrm/openssl4j/latest/${DIR}/${FILE}.txt > ./openssl4j/src/main/resources/objects/${FILE}.txt
      curl https://dl.bintray.com/sfuhrm/openssl4j/latest/${DIR}/${FILE} > ./openssl4j/src/main/resources/objects/${FILE}
    done
  done
done
