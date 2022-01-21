####
#
# Makefile for generating the native C library
#
####
JAVA_OS_ARCH:=$(shell cd build-helper && ${JAVA_HOME}/bin/java -Xint OsArch.java )

JNI_JAVA_SOURCES=openssl4j/src/main/java
JNI_C_SOURCES=openssl4j/src/main/c
TARGET=target
INSTALL_TARGET=openssl4j/src/main/resources/objects
JNI_JAVA_FILES=${JNI_JAVA_SOURCES}/de/sfuhrm/openssl4j/OpenSSLMessageDigestNative.java ${JNI_JAVA_SOURCES}/de/sfuhrm/openssl4j/OpenSSLCipherNative.java
JNI_HEADER_FILES=${TARGET}/include/de_sfuhrm_openssl4j_OpenSSLMessageDigestNative.h ${TARGET}/include/de_sfuhrm_openssl4j_OpenSSLCipherNative.h

.PHONY: all
.PHONY: clean
.PHONY: install

all: ${TARGET}/libopenssl4j-${JAVA_OS_ARCH}.so
clean:
	rm -fr ${TARGET}

install: ${TARGET}/libopenssl4j-${JAVA_OS_ARCH}.so
	cp $< ${INSTALL_TARGET}

${TARGET}/include/%.h: ${JNI_JAVA_FILES}
	mkdir -p ${TARGET}/include
	${JAVA_HOME}/bin/javac -classpath ${JNI_JAVA_SOURCES} -h ${TARGET}/include -d ${TARGET} -s ${TARGET} ${JNI_JAVA_FILES}

${TARGET}/libopenssl4j-${JAVA_OS_ARCH}.so: ${JNI_HEADER_FILES}
	gcc -Wall -Werror -fPIC -o $@ -lc -lssl -shared -I${TARGET}/include/ \
	-I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux \
	${JNI_C_SOURCES}/openssl4j_common.c \
	${JNI_C_SOURCES}/openssl4j_messagedigest.c \
	${JNI_C_SOURCES}/openssl4j_cipher.c
