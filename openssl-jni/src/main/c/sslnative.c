/*
** OpenSSL MD5 to Java Binding Code
*/

#include <stdlib.h>
#include <string.h>
#include <openssl/evp.h>
#include <openssl/ossl_typ.h>

#include "de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative.h"

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

JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative_digestLength
  (JNIEnv *env, jclass clazz, jobject context) {
    EVP_MD_CTX *mdctx = md_context_from(env, context);
    return EVP_MD_CTX_size(mdctx);
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative_removeContext
  (JNIEnv *env, jclass clazz, jobject context) {
    EVP_MD_CTX *mdctx = md_context_from(env, context);
	EVP_MD_CTX_free(mdctx);
}

struct StringArrayPosition {
    jint index;
    jint length;
    JNIEnv *env;
    jobjectArray array;
};


/* Callback for EVP_MD_do_all_sorted that counts the number of MD algorithms. */
static void EVP_MD_do_all_count_func(const EVP_MD *ciph, const char *from, const char *to, void *x) {
    if (ciph != NULL) {
        jint *numOfAlgos = (jint*)x;
        (*numOfAlgos)++;
    }
}

/* Callback for EVP_MD_do_all_sorted that sets the string array elements.
** @param ciph cipher, can be NULL if this is an alias.
** @param from the name of the algorithm.
** @param to NULL if this is not an alias, or the target EVP_MD if this is a an alias.
** @param x the last param passed to the EVP_MD_do_all_sorted() call.
*/
static void EVP_MD_do_all_string_array_set(const EVP_MD *ciph, const char *from, const char *to, void *x) {
    struct StringArrayPosition *sap = (struct StringArrayPosition*)x;
    if (ciph == NULL) {
        // alias
        return;
    }
    const char *evp_name = EVP_MD_name(ciph);

    jstring algoNameString = (*sap->env)->NewStringUTF(sap->env, evp_name);
    if (algoNameString == NULL) {
        return;
    }

    (*sap->env)->SetObjectArrayElement(sap->env, sap->array, sap->index, algoNameString);
    sap->index++;
}

JNIEXPORT jobjectArray JNICALL Java_de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative_listMessageDigests
  (JNIEnv *env, jclass clazz) {
  struct StringArrayPosition sap;
  jobjectArray result = NULL;

  sap.index = 0;
  sap.length = 0;
  sap.env = env;
  sap.array = NULL;

  EVP_MD_do_all_sorted(EVP_MD_do_all_count_func, &sap.length);
  jclass stringClass = (*env)->FindClass(env, "java/lang/String");
  if (stringClass == NULL) {
    return NULL;
  }

  result = (*env)->NewObjectArray(env, sap.length, stringClass, NULL);
  sap.array = result;

  EVP_MD_do_all_sorted(EVP_MD_do_all_string_array_set, &sap);

  return result;
}

JNIEXPORT jobject JNICALL Java_de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative_nativeContext
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

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative_nativeUpdateWithByte
    (JNIEnv *env, jobject obj, jobject context, jbyte byteData) {
      EVP_MD_CTX* context_data = md_context_from(env, context);
      if (context_data != NULL) {
  	    if (1 != EVP_DigestUpdate(context_data, &byteData, 1)) {
             throw_error(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestUpdate failed");
  	    }
      }
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative_nativeUpdateWithByteArray
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

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative_nativeUpdateWithByteBuffer
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

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative_nativeFinal
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

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl_jni_OpenSSLMessageDigestNative_nativeInit
  (JNIEnv *env, jobject obj, jobject context, jstring jalgoName) {
    EVP_MD_CTX* context_data = md_context_from(env, context);
    if (context_data == NULL) {
        throw_error(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestInit_ex failed");
        return;
     }

    jsize nameLength = (*env)->GetStringUTFLength(env, jalgoName);

    char javaNameC[256];
    if (nameLength > sizeof(javaNameC)) {
        throw_error(env, ILLEGAL_STATE_EXCEPTION, "Algorithm name exceeds the limit");
        return;
    }

    jboolean isCopy;
    const char * cstr = (*env)->GetStringUTFChars(env, jalgoName, &isCopy);
    strncpy(javaNameC, cstr, sizeof(javaNameC));
    (*env)->ReleaseStringUTFChars(env, jalgoName, cstr);

    const EVP_MD *evp_md = EVP_get_digestbyname(javaNameC);
    if (evp_md == NULL) {
        throw_error(env, ILLEGAL_STATE_EXCEPTION, "Named MessageDigest was not found");
        return;
    }

    if (1 != EVP_DigestInit_ex(context_data, evp_md, NULL)) {
        throw_error(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestInit_ex failed");
        return;
    }
}
