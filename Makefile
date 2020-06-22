####
#
# Makefile for generating the native C library
#
####
JNI_JAVA_SOURCES=openssl-jni/src/main/java
JNI_C_SOURCES=openssl-jni/src/main/c

.PHONY: all
.PHONY: clean

all: target/libsslnative-OSNAME-OSARCH.so
clean:
	rm -fr target

target/include/de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative.h: ${JNI_JAVA_SOURCES}/de/sfuhrm/openssl/jni/OpenSSLMessageDigestNative.java
	mkdir -p target/include
	javac -classpath ${JNI_JAVA_SOURCES} -h target/include -d target -s target $<

target/libsslnative-OSNAME-OSARCH.so: target/include/de_sfuhrm_openssl_jni_OpenSSLMessageDigestNatived.h
	gcc -Wall -Werror -o target/libsslnative-OSNAME-OSARCH.so -lc -lssl -shared -Itarget/include/ -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux ${JNI_C_SOURCES}/sslnative.c
