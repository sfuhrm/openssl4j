####
#
# Makefile for generating the native C library
#
####

ifndef JAVA_HOME
	echo "Need environment variable JAVA_HOME"; exit 1
endif

JAVA_OS_ARCH:=$(shell ${JAVA_HOME}/bin/java build-helper/OsArch.java )

JNI_JAVA_SOURCES=openssl4j/src/main/java
JNI_C_SOURCES=openssl4j/src/main/c
TARGET=target
INSTALL_TARGET=openssl4j/src/main/resources/objects

.PHONY: all
.PHONY: clean
.PHONY: install

all: ${TARGET}/libopenssl4j-${JAVA_OS_ARCH}.so
clean:
	rm -fr ${TARGET}

install: ${TARGET}/libopenssl4j-${JAVA_OS_ARCH}.so
	cp $< ${INSTALL_TARGET}

${TARGET}/include/de_sfuhrm_openssl4j_OpenSSLMessageDigestNative.h: ${JNI_JAVA_SOURCES}/de/sfuhrm/openssl4j/OpenSSLMessageDigestNative.java
	mkdir -p ${TARGET}/include
	javac -classpath ${JNI_JAVA_SOURCES} -h ${TARGET}/include -d ${TARGET} -s ${TARGET} $<

${TARGET}/libopenssl4j-${JAVA_OS_ARCH}.so: ${TARGET}/include/de_sfuhrm_openssl4j_OpenSSLMessageDigestNative.h
	gcc -Wall -Werror -o $@ -lc -lssl -shared -I${TARGET}/include/ \
	-I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux \
	${JNI_C_SOURCES}/openssl4j.c
