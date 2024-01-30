/*
** OpenSSL Common Java Binding Code
** OpenSSL3 HMAC Wrapper
** @author Daniel Thertell
*/

#include "openssl4j.h"

#include <stdlib.h>
#include <openssl/hmac.h>

#include "de_sfuhrm_openssl4j_OpenSSLMacNative.h"

// Returns max MAC length.
JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl4j_OpenSSLMacNative_getMaxMacLength(JNIEnv *env, jclass clazz) {
    return EVP_MAX_MD_SIZE;
}

// Calculates an HMAC for the given data.
JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl4j_OpenSSLMacNative_hmacNative(JNIEnv *env, jclass clazz, jlong libCtx, jstring algName, jbyteArray key, jint keyLen, jbyteArray data, jint dataLen, jbyteArray output) {
    const char* cAlgName = (*env)->GetStringUTFChars(env, algName, 0);
    EVP_MD* digest = GetMessageDigest(cAlgName, libCtx);
    
    const unsigned char* cKey = (const unsigned char*)((*env)->GetByteArrayElements(env, key, 0));
    const unsigned char* cData = (const unsigned char*)((*env)->GetByteArrayElements(env, data, 0));
    unsigned char* cOutput = (unsigned char*)((*env)->GetByteArrayElements(env, output, 0));

    unsigned int outputLen = 0;

    unsigned char* res = HMAC(digest, cKey, keyLen, cData, dataLen, cOutput, &outputLen);

    // Cleanup.
    (*env)->ReleaseByteArrayElements(env, key, (jbyte*)cKey, 0);
    (*env)->ReleaseByteArrayElements(env, data, (jbyte*)cData, 0);
    (*env)->ReleaseByteArrayElements(env, output, (jbyte*)cOutput, 0);
    (*env)->ReleaseStringUTFChars(env, algName, cAlgName);

    if(res == NULL) {
        throwErrorWithOpenSSLInternalError(env, UNSUPPORTED_OPERATION_EXCEPTION, "Error calculating hmac");
        return -1;
    }

    return outputLen;
}