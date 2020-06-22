####
#
# Makefile for generating the native C library
#
####

ifndef JAVA_HOME
	echo "Need environment variable JAVA_HOME"; exit 1
endif

JAVA_OS_ARCH:=$(shell ${JAVA_HOME}/bin/java build-helper/OsArch.java )

JNI_JAVA_SOURCES=openssl-jni/src/main/java
JNI_C_SOURCES=openssl-jni/src/main/c
TARGET=target

.PHONY: all
.PHONY: clean

all: ${TARGET}/libsslnative-${JAVA_OS_ARCH}.so
clean:
	rm -fr ${TARGET}

${TARGET}/include/de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative.h: ${JNI_JAVA_SOURCES}/de/sfuhrm/openssl/jni/OpenSSLMessageDigestNative.java
	mkdir -p ${TARGET}/include
	javac -classpath ${JNI_JAVA_SOURCES} -h ${TARGET}/include -d ${TARGET} -s ${TARGET} $<

${TARGET}/libsslnative-${JAVA_OS_ARCH}.so: ${TARGET}/include/de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative.h
	gcc -Wall -Werror -o $@ -lc -lssl -shared -I${TARGET}/include/ \
	-I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux \
	${JNI_C_SOURCES}/sslnative.c
