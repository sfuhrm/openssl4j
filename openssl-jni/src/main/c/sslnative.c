/*
** OpenSSL MD5 to Java Binding Code
*/

#include <stdlib.h>
#include <openssl/md5.h>
#include <openssl/sha.h>

#include "de_sfuhrm_openssl_jni_MD5Native.h"
#include "de_sfuhrm_openssl_jni_SHA1Native.h"

#define ILLEGAL_STATE_EXCEPTION "java/lang/IllegalStateException"

static void throw_error(JNIEnv *env, const char *exceptionClassName, const char *message) {
    jclass exceptionClass = (*env)->FindClass(env, exceptionClassName);
    if (exceptionClass != NULL) {
        jint success = (*env)->ThrowNew(env, exceptionClass, message);
        if (!success) {
            (*env)->FatalError(env, "Could not throw exception");
        }
    } else {
        (*env)->FatalError(env, "Didn't find IllegalStateException class");
    }
}

static void* md_context_from(JNIEnv *env, jobject context) {
    void* context_data = (void*) (*env)->GetDirectBufferAddress(env, context);
    if (context_data == NULL) {
        throw_error(env, ILLEGAL_STATE_EXCEPTION, "GetDirectBufferAddress() for Context failed");
    }
    return context_data;
}

#define DIGEST_LENGTH 16
#define CONTEXT_T MD5_CTX
#define C_INIT_FUNC MD5_Init
#define C_UPDATE_FUNC MD5_Update
#define C_FINAL_FUNC MD5_Final

#define NATIVE_CONTEXT_SIZE Java_de_sfuhrm_openssl_jni_MD5Native_nativeContextSize
#define NATIVE_INIT Java_de_sfuhrm_openssl_jni_MD5Native_nativeInit
#define NATIVE_UPDATE_BYTE Java_de_sfuhrm_openssl_jni_MD5Native_nativeUpdateWithByte
#define NATIVE_UPDATE_BYTE_ARRAY Java_de_sfuhrm_openssl_jni_MD5Native_nativeUpdateWithByteArray
#define NATIVE_UPDATE_BYTE_BUFFER Java_de_sfuhrm_openssl_jni_MD5Native_nativeUpdateWithByteBuffer
#define NATIVE_FINAL Java_de_sfuhrm_openssl_jni_MD5Native_nativeFinal

#include "mdnative.h"

/* sha1 */
#undef DIGEST_LENGTH
#undef CONTEXT_T
#undef C_INIT_FUNC
#undef C_UPDATE_FUNC
#undef C_FINAL_FUNC
#undef NATIVE_CONTEXT_SIZE
#undef NATIVE_INIT
#undef NATIVE_UPDATE_BYTE
#undef NATIVE_UPDATE_BYTE_ARRAY
#undef NATIVE_UPDATE_BYTE_BUFFER
#undef NATIVE_FINAL

#define DIGEST_LENGTH 20
#define CONTEXT_T SHA_CTX
#define C_INIT_FUNC SHA1_Init
#define C_UPDATE_FUNC SHA1_Update
#define C_FINAL_FUNC SHA1_Final

#define NATIVE_CONTEXT_SIZE Java_de_sfuhrm_openssl_jni_SHA1Native_nativeContextSize
#define NATIVE_INIT Java_de_sfuhrm_openssl_jni_SHA1Native_nativeInit
#define NATIVE_UPDATE_BYTE Java_de_sfuhrm_openssl_jni_SHA1Native_nativeUpdateWithByte
#define NATIVE_UPDATE_BYTE_ARRAY Java_de_sfuhrm_openssl_jni_SHA1Native_nativeUpdateWithByteArray
#define NATIVE_UPDATE_BYTE_BUFFER Java_de_sfuhrm_openssl_jni_SHA1Native_nativeUpdateWithByteBuffer
#define NATIVE_FINAL Java_de_sfuhrm_openssl_jni_SHA1Native_nativeFinal

#include "mdnative.h"
