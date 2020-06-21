/*
** OpenSSL MD5 to Java Binding Code
*/

#include <stdlib.h>
#include <openssl/evp.h>
#include <openssl/ossl_typ.h>

#include "de_sfuhrm_openssl_jni_AbstractNative.h"
#include "de_sfuhrm_openssl_jni_MD5Native.h"
#include "de_sfuhrm_openssl_jni_SHA1Native.h"
#include "de_sfuhrm_openssl_jni_SHA224Native.h"
#include "de_sfuhrm_openssl_jni_SHA256Native.h"
#include "de_sfuhrm_openssl_jni_SHA384Native.h"
#include "de_sfuhrm_openssl_jni_SHA512Native.h"
#include "de_sfuhrm_openssl_jni_SHA512_224Native.h"
#include "de_sfuhrm_openssl_jni_SHA512_256Native.h"
#include "de_sfuhrm_openssl_jni_SHA3_224Native.h"
#include "de_sfuhrm_openssl_jni_SHA3_256Native.h"
#include "de_sfuhrm_openssl_jni_SHA3_384Native.h"
#include "de_sfuhrm_openssl_jni_SHA3_512Native.h"

#define ILLEGAL_STATE_EXCEPTION "java/lang/IllegalStateException"
#define UNSUPPORTED_OPERATION_EXCEPTION "java/lang/UnsupportedOperationException"

static void throw_error(JNIEnv *env, const char *exceptionClassName, const char *message) {
    jclass exceptionClass = (*env)->FindClass(env, exceptionClassName);
    if (exceptionClass != NULL) {
        jint success = (*env)->ThrowNew(env, exceptionClass, message);
        if (0 != success) {
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

/*
 * Class:     de_sfuhrm_openssl_jni_AbstractNative
 * Method:    digestLength
 * Signature: (Ljava/nio/ByteBuffer;)I
 */
JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl_jni_AbstractNative_digestLength
  (JNIEnv *env, jclass clazz, jobject context) {
    EVP_MD_CTX *mdctx = md_context_from(env, context);
    return EVP_MD_CTX_size(mdctx);
}


JNIEXPORT jobject JNICALL Java_de_sfuhrm_openssl_jni_AbstractNative_nativeContext
  (JNIEnv *env, jobject obj) {
    EVP_MD_CTX *mdctx;

	if ((mdctx = EVP_MD_CTX_new()) == NULL) {
        throw_error(env, ILLEGAL_STATE_EXCEPTION, "Could not allocate context");
        return NULL;
	}

    /* TODO 256 is just to satisfy the call */
    jobject result = (*env)->NewDirectByteBuffer(env, mdctx, 256);
    if (result == NULL) {
        throw_error(env, ILLEGAL_STATE_EXCEPTION, "Could not NewDirectByteBuffer()");
    }

    return result;
}

/*
 * Class:     de_sfuhrm_openssl_jni_AbstractNative
 * Method:    removeContext
 * Signature: (Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_AbstractNative_removeContext
  (JNIEnv *env, jclass clazz, jobject context) {
    EVP_MD_CTX *mdctx = md_context_from(env, context);
	EVP_MD_CTX_free(mdctx);
}

/*
 * Class:     de_sfuhrm_openssl_jni_AbstractNative
 * Method:    nativeUpdateWithByte
 * Signature: (Ljava/nio/ByteBuffer;B)V
 */
JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_AbstractNative_nativeUpdateWithByte
    (JNIEnv *env, jobject obj, jobject context, jbyte byteData) {
      EVP_MD_CTX* context_data = md_context_from(env, context);
      if (context_data != NULL) {
  	    if (1 != EVP_DigestUpdate(context_data, &byteData, 1)) {
             throw_error(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestUpdate failed");
  	    }
      }
}

/*
 * Class:     de_sfuhrm_openssl_jni_AbstractNative
 * Method:    nativeUpdateWithByteArray
 * Signature: (Ljava/nio/ByteBuffer;[BII)V
 */
JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_AbstractNative_nativeUpdateWithByteArray
  (JNIEnv *env, jobject obj, jobject context, jbyteArray jarray, jint offset, jint length) {
    EVP_MD_CTX* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        jboolean isCopy = JNI_FALSE;

        /* TODO this copies the whole array, even if length is 1 byte */
        jbyte *carray = (*env)->GetByteArrayElements(env, jarray, &isCopy);
        if (carray != NULL) {
    	    if (1 != EVP_DigestUpdate(context_data, carray + offset, length)) {
               throw_error(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestUpdate failed");
	        }
            /* JNI_ABORT: Don't copy back the array, nothing has changed */
            (*env)->ReleaseByteArrayElements(env, jarray, carray, JNI_ABORT);
        } else {
            throw_error(env, ILLEGAL_STATE_EXCEPTION, "GetByteArrayElements for array failed");
        }
    }
}

/*
 * Class:     de_sfuhrm_openssl_jni_AbstractNative
 * Method:    nativeUpdateWithByteBuffer
 * Signature: (Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;II)V
 */
JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_AbstractNative_nativeUpdateWithByteBuffer
  (JNIEnv *env, jobject obj, jobject context, jobject bb, jint offset, jint length) {
    EVP_MD_CTX* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        jbyte* buffer = (*env)->GetDirectBufferAddress(env, bb);
        if (buffer != NULL) {
            jbyte* offset_buffer = buffer + offset;

    	    if (1 != EVP_DigestUpdate(context_data, offset_buffer, length)) {
               throw_error(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestUpdate failed");
	        }
        } else {
            throw_error(env, ILLEGAL_STATE_EXCEPTION, "GetDirectBufferAddress for ByteBuffer failed");
        }
    }
}

/*
 * Class:     de_sfuhrm_openssl_jni_AbstractNative
 * Method:    nativeFinal
 * Signature: (Ljava/nio/ByteBuffer;[B)V
 */
JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_AbstractNative_nativeFinal
  (JNIEnv *env, jobject obj, jobject context, jbyteArray jdigest) {
    EVP_MD_CTX* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        jbyte cdigest[EVP_MAX_MD_SIZE];
        unsigned int actualSize;
  	    if (1 != EVP_DigestFinal_ex(context_data, (unsigned char*)cdigest, &actualSize)) {
            throw_error(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestFinal_ex failed");
	    }
        (*env)->SetByteArrayRegion(env, jdigest, 0, actualSize, cdigest);
    }
}


/* Generates an initialization function.
 * @param jni_func_name the function name as defined in the Javah-generated header files.
 * @param openssl_evp_name the OpenSSL evp function name as defined in openssl/evp.h
 */
#define INIT_FUNC(jni_func_name, openssl_evp_name) JNIEXPORT void JNICALL jni_func_name \
  (JNIEnv *env, jobject obj, jobject context) { \
    EVP_MD_CTX* context_data = md_context_from(env, context); \
    if (context_data != NULL) { \
    	if (1 != EVP_DigestInit_ex(context_data, openssl_evp_name(), NULL)) { \
           throw_error(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestInit_ex failed"); \
    	} \
    } \
}

/* Generates an initialization function.
 * @param jni_func_name the function name as defined in the Javah-generated header files.
 */
#define THROW_UNSUPPORTED_OPERATION_EXCEPTION(jni_func_name) JNIEXPORT void JNICALL jni_func_name \
  (JNIEnv *env, jobject obj, jobject context) { \
    throw_error(env, UNSUPPORTED_OPERATION_EXCEPTION, "Algorithm is not supported"); \
}

#ifndef OPENSSL_NO_MD5
INIT_FUNC(Java_de_sfuhrm_openssl_jni_MD5Native_nativeInit,EVP_md5)
#else
THROW_UNSUPPORTED_OPERATION_EXCEPTION(Java_de_sfuhrm_openssl_jni_MD5Native_nativeInit)
#endif

INIT_FUNC(Java_de_sfuhrm_openssl_jni_SHA1Native_nativeInit,EVP_sha1)
INIT_FUNC(Java_de_sfuhrm_openssl_jni_SHA224Native_nativeInit,EVP_sha224)
INIT_FUNC(Java_de_sfuhrm_openssl_jni_SHA256Native_nativeInit,EVP_sha256)
INIT_FUNC(Java_de_sfuhrm_openssl_jni_SHA384Native_nativeInit,EVP_sha384)
INIT_FUNC(Java_de_sfuhrm_openssl_jni_SHA512Native_nativeInit,EVP_sha512)
INIT_FUNC(Java_de_sfuhrm_openssl_jni_SHA512_1224Native_nativeInit,EVP_sha512_224)
INIT_FUNC(Java_de_sfuhrm_openssl_jni_SHA512_1256Native_nativeInit,EVP_sha512_256)

INIT_FUNC(Java_de_sfuhrm_openssl_jni_SHA3_1224Native_nativeInit,EVP_sha3_224)
INIT_FUNC(Java_de_sfuhrm_openssl_jni_SHA3_1256Native_nativeInit,EVP_sha3_256)
INIT_FUNC(Java_de_sfuhrm_openssl_jni_SHA3_1384Native_nativeInit,EVP_sha3_384)
INIT_FUNC(Java_de_sfuhrm_openssl_jni_SHA3_1512Native_nativeInit,EVP_sha3_512)
