/*
** OpenSSL MD5 to Java Binding Code
*/

#include <stdlib.h>
#include <openssl/md5.h>
#include "de_sfuhrm_openssl_jni_MD5Native.h"

#define DIGEST_LENGTH 16
#define ILLEGAL_STATE_EXCEPTION "java/lang/IllegalStateException"

static void throw_error(JNIEnv *env, const char *exceptionClassName, const char *message) {
    jclass exceptionClass = (*env)->FindClass(env, exceptionClassName);
    if (exceptionClass != NULL) {
        jint ignored = (*env)->ThrowNew(env, exceptionClass, message);
    } else {
        (*env)->FatalError(env, "Didn't find IllegalStateException class");
    }
}

static MD5_CTX* md_context_from(JNIEnv *env, jobject context) {
    MD5_CTX* context_data = (MD5_CTX*) (*env)->GetDirectBufferAddress(env, context);
    if (context_data == NULL) {
        throw_error(env, ILLEGAL_STATE_EXCEPTION, "GetDirectBufferAddress() for Context failed");
    }
    return context_data;
}

JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl_jni_MD5Native_nativeContextSize
  (JNIEnv *env, jclass clazz) {
    return sizeof(MD5_CTX);
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_MD5Native_nativeInit
  (JNIEnv *env, jclass clazz, jobject context) {
    MD5_CTX* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        MD5_Init(context_data);
    }
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_MD5Native_nativeUpdateWithByte
  (JNIEnv *env, jclass clazz, jobject context, jbyte byteData) {
    MD5_CTX* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        MD5_Update(context_data, &byteData, 1);
    }
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_MD5Native_nativeUpdateWithByteArray
  (JNIEnv *env, jclass clazz, jobject context, jbyteArray jarray, jint offset, jint length) {
    MD5_CTX* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        jboolean isCopy = JNI_FALSE;

        /* TODO this copies the whole array, even if length is 1 byte */
        jbyte *carray = (*env)->GetByteArrayElements(env, jarray, &isCopy);
        if (carray != NULL) {
            MD5_Update(context_data, carray + offset, length);
            /* JNI_ABORT: Don't copy back the array, nothing has changed */
            (*env)->ReleaseByteArrayElements(env, jarray, carray, JNI_ABORT);
        } else {
            throw_error(env, ILLEGAL_STATE_EXCEPTION, "GetByteArrayElements for array failed");
        }
    }
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_MD5Native_nativeUpdateWithByteBuffer
  (JNIEnv *env, jclass clazz, jobject context, jobject bb, jint offset, jint length) {
    MD5_CTX* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        jbyte* buffer = (*env)->GetDirectBufferAddress(env, bb);
        if (buffer != NULL) {
            jlong capacity = (*env)->GetDirectBufferCapacity(env,  bb);
            jbyte* offset_buffer = buffer + offset;

            MD5_Update(context_data, offset_buffer, length);
        } else {
            throw_error(env, ILLEGAL_STATE_EXCEPTION, "GetDirectBufferAddress for ByteBuffer failed");
        }
    }
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_MD5Native_nativeFinal
  (JNIEnv *env, jclass clazz, jobject context, jbyteArray jdigest) {
    MD5_CTX* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        jbyte cdigest[DIGEST_LENGTH];

        MD5_Final(cdigest, context_data);
        (*env)->SetByteArrayRegion(env, jdigest, 0, DIGEST_LENGTH, cdigest);
    }
}
