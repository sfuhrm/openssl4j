/*
** OpenSSL MD5 to Java Binding Code
*/

#include <stdlib.h>
#include <openssl/md5.h>
#include <openssl/sha.h>

#include "de_sfuhrm_openssl_jni_MD5Native.h"
#include "de_sfuhrm_openssl_jni_SHA1Native.h"
#include "de_sfuhrm_openssl_jni_SHA224Native.h"
#include "de_sfuhrm_openssl_jni_SHA256Native.h"

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

#define DIGEST_LENGTH MD5_DIGEST_LENGTH
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
#include "ssl_undef.h"

#define DIGEST_LENGTH SHA_DIGEST_LENGTH
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

/* sha-224 */
#include "ssl_undef.h"

#define DIGEST_LENGTH SHA224_DIGEST_LENGTH
#define CONTEXT_T SHA256_CTX
#define C_INIT_FUNC SHA224_Init
#define C_UPDATE_FUNC SHA224_Update
#define C_FINAL_FUNC SHA224_Final

#define NATIVE_CONTEXT_SIZE Java_de_sfuhrm_openssl_jni_SHA224Native_nativeContextSize
#define NATIVE_INIT Java_de_sfuhrm_openssl_jni_SHA224Native_nativeInit
#define NATIVE_UPDATE_BYTE Java_de_sfuhrm_openssl_jni_SHA224Native_nativeUpdateWithByte
#define NATIVE_UPDATE_BYTE_ARRAY Java_de_sfuhrm_openssl_jni_SHA224Native_nativeUpdateWithByteArray
#define NATIVE_UPDATE_BYTE_BUFFER Java_de_sfuhrm_openssl_jni_SHA224Native_nativeUpdateWithByteBuffer
#define NATIVE_FINAL Java_de_sfuhrm_openssl_jni_SHA224Native_nativeFinal

#include "mdnative.h"

/* sha-256 */
#include "ssl_undef.h"

#define DIGEST_LENGTH SHA256_DIGEST_LENGTH
#define CONTEXT_T SHA256_CTX
#define C_INIT_FUNC SHA256_Init
#define C_UPDATE_FUNC SHA256_Update
#define C_FINAL_FUNC SHA256_Final

#define NATIVE_CONTEXT_SIZE Java_de_sfuhrm_openssl_jni_SHA256Native_nativeContextSize
#define NATIVE_INIT Java_de_sfuhrm_openssl_jni_SHA256Native_nativeInit
#define NATIVE_UPDATE_BYTE Java_de_sfuhrm_openssl_jni_SHA256Native_nativeUpdateWithByte
#define NATIVE_UPDATE_BYTE_ARRAY Java_de_sfuhrm_openssl_jni_SHA256Native_nativeUpdateWithByteArray
#define NATIVE_UPDATE_BYTE_BUFFER Java_de_sfuhrm_openssl_jni_SHA256Native_nativeUpdateWithByteBuffer
#define NATIVE_FINAL Java_de_sfuhrm_openssl_jni_SHA256Native_nativeFinal

#include "mdnative.h"
