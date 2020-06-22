####
#
# Makefile for generating the native C library
#
####
JNI_JAVA_SOURCES=openssl-jni/src/main/java
JNI_C_SOURCES=openssl-jni/src/main/c
TARGET=target

.PHONY: all
.PHONY: clean

all: ${TARGET}/libsslnative-OSNAME-OSARCH.so
clean:
	rm -fr ${TARGET}

${TARGET}/include/de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative.h: ${JNI_JAVA_SOURCES}/de/sfuhrm/openssl/jni/OpenSSLMessageDigestNative.java
	mkdir -p ${TARGET}/include
	javac -classpath ${JNI_JAVA_SOURCES} -h ${TARGET}/include -d ${TARGET} -s ${TARGET} $<

${TARGET}/libsslnative-OSNAME-OSARCH.so: ${TARGET}/include/de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative.h
	gcc -Wall -Werror -o ${TARGET}/libsslnative-OSNAME-OSARCH.so -lc -lssl -shared -I${TARGET}/include/ \
	-I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux \
	${JNI_C_SOURCES}/sslnative.c
