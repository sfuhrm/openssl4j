/*
** OpenSSL Common Java Binding Code
** OpenSSL3 Secure Random wrapper
** @author Daniel Thertell
*/

#include "openssl4j.h"

#include <stdlib.h>
#include <openssl/rand.h>

#include "de_sfuhrm_openssl4j_OpenSSLSecureRandomNative.h"

// Sets a seed for the secure-random engine by adding the given bytes.
JNIEXPORT void JNICALL Java_de_sfuhrm_openssl4j_OpenSSLSecureRandomNative_engineSetSeedNative(JNIEnv *env, jclass clazz, jbyteArray seed, jint seedLen, jdouble seedRandomness) {
    const unsigned char *seedBytes = (const unsigned char*)((*env)->GetByteArrayElements(env, seed, 0));
    RAND_add(seedBytes, seedLen, seedRandomness);
   (*env)->ReleaseByteArrayElements(env, seed, (jbyte*)seedBytes, 0);
}

// Pulls X bytes from the secure-random source. Throws an error if not possible. 
JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl4j_OpenSSLSecureRandomNative_engineNextBytesNative(JNIEnv *env, jclass clazz, jbyteArray nextBytesArr, jint nextBytesArrLen) {
    unsigned char *nextBytesC = (unsigned char*)((*env)->GetByteArrayElements(env, nextBytesArr, 0));
    int res = RAND_bytes(nextBytesC, nextBytesArrLen);
    (*env)->ReleaseByteArrayElements(env, nextBytesArr, (jbyte*)nextBytesC, 0);

    if(res == -1) {
        // Not supported.
        throwErrorWithOpenSSLInternalError(env, UNSUPPORTED_OPERATION_EXCEPTION, "Secure random is not supported on this hardware");
    }else if(res == 0) {
        // Other error.
        throwErrorWithOpenSSLInternalError(env, UNSUPPORTED_OPERATION_EXCEPTION, "error getting random bytess");
    }

    return res;
}