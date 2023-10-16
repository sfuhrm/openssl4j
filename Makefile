####
#
# Makefile for generating the native C library
#
####
JAVA_OS_ARCH:=$(shell cd build-helper && ${JAVA_HOME}/bin/java -Xint OsArch.java )
MACHINE_TRIPLET=$(shell gcc -dumpmachine)

JNI_JAVA_SOURCES=openssl4j/src/main/java
JNI_C_SOURCES=openssl4j-objects/src/main/c
TARGET=target
INSTALL_TARGET=openssl4j-objects/src/main/resources/objects
JNI_JAVA_FILES=${JNI_JAVA_SOURCES}/de/sfuhrm/openssl4j/OpenSSLMessageDigestNative.java
JNI_HEADER_FILES=${TARGET}/include/de_sfuhrm_openssl4j_OpenSSLMessageDigestNative.h

.PHONY: all
.PHONY: clean
.PHONY: install

install: ${TARGET}/libopenssl4j-${JAVA_OS_ARCH}.so
	mkdir -p ${INSTALL_TARGET}
	cp $< ${INSTALL_TARGET}

clean:
	rm -fr ${TARGET} ${INSTALL_TARGET}

${TARGET}/include/%.h: ${JNI_JAVA_FILES}
	mkdir -p ${TARGET}/include
	${JAVA_HOME}/bin/javac -J-Xint -classpath ${JNI_JAVA_SOURCES} -h ${TARGET}/include -d ${TARGET} -s ${TARGET} ${JNI_JAVA_FILES}

${TARGET}/%.o: ${JNI_C_SOURCES}/%.c ${JNI_HEADER_FILES}
	gcc -Wall -Werror -fPIC -c -o $@ \
	-I${TARGET}/include/ \
	-I${JAVA_HOME}/include \
	-I${JAVA_HOME}/include/linux \
	$<

${TARGET}/libopenssl4j-${JAVA_OS_ARCH}.so: ${TARGET}/openssl4j_common.o ${TARGET}/openssl4j_messagedigest.o
	ld --verbose --pic-executable -fPIC -shared -o $@ /usr/lib/${MACHINE_TRIPLET}/libssl.a -lc \
	${TARGET}/openssl4j_common.o \
	${TARGET}/openssl4j_messagedigest.o
