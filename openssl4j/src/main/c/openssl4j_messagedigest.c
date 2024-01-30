/*
** OpenSSL MessageDigest to Java Binding Code.
**
** See here for an example of the EVP API:
** https://wiki.openssl.org/index.php/EVP_Message_Digests
** @author Stephan Fuhrmann
*/

#include <stdlib.h>
#include <string.h>
#include <openssl/evp.h>

#ifdef __APPLE__
#include <malloc/malloc.h>
#elif __linux__
#include <malloc.h>
#endif

#include "openssl4j.h"

#include "de_sfuhrm_openssl4j_OpenSSLMessageDigestNative.h"

#if OPENSSL_VERSION_NUMBER >= 0x10101000L
#define OPENSSL_MD_NEW_FUNC EVP_MD_CTX_new
#define OPENSSL_MD_FREE_FUNC EVP_MD_CTX_free
#else
#define OPENSSL_MD_NEW_FUNC EVP_MD_CTX_create
#define OPENSSL_MD_FREE_FUNC EVP_MD_CTX_destroy
#endif

JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl4j_OpenSSLMessageDigestNative_digestLength
  (JNIEnv *env, jclass clazz, jobject context) {
    EVP_MD_CTX *mdctx = get_context_from(env, context);
    if (mdctx != NULL) {
        return EVP_MD_CTX_size(mdctx);
    }
    return 0;
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl4j_OpenSSLMessageDigestNative_removeContext
  (JNIEnv *env, jclass clazz, jobject context) {
    EVP_MD_CTX *mdctx = get_context_from(env, context);
    if (mdctx != NULL) {
        OPENSSL_MD_FREE_FUNC(mdctx);
    }
}


/* Callback for EVP_MD_do_all that counts the number of MD algorithms. */
static void EVP_MD_do_all_count_func(const EVP_MD *ciph, const char *from, const char *to, void *x) {
    if (ciph != NULL) {
        jint *numOfAlgos = (jint*)x;
        (*numOfAlgos)++;
    }
}

/* Callback for EVP_MD_do_all that sets the string array elements.
** @param ciph cipher, can be NULL if this is an alias.
** @param from the name of the algorithm.
** @param to NULL if this is not an alias, or the target EVP_MD if this is a an alias.
** @param x the last param passed to the EVP_MD_do_all() call.
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

JNIEXPORT jobjectArray JNICALL Java_de_sfuhrm_openssl4j_OpenSSLMessageDigestNative_listMessageDigests
  (JNIEnv *env, jclass clazz) {
  struct StringArrayPosition sap;
  jobjectArray result = NULL;

  sap.index = 0;
  sap.length = 0;
  sap.env = env;
  sap.array = NULL;

  EVP_MD_do_all(EVP_MD_do_all_count_func, &sap.length);
  jclass stringClass = (*env)->FindClass(env, "java/lang/String");
  if (stringClass == NULL) {
    return NULL;
  }

  result = (*env)->NewObjectArray(env, sap.length, stringClass, NULL);
  sap.array = result;

  EVP_MD_do_all(EVP_MD_do_all_string_array_set, &sap);

  return result;
}

JNIEXPORT jobject JNICALL Java_de_sfuhrm_openssl4j_OpenSSLMessageDigestNative_nativeContext
  (JNIEnv *env, jobject obj) {
    EVP_MD_CTX *mdctx;

	if ((mdctx = OPENSSL_MD_NEW_FUNC()) == NULL) {
        throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not allocate context");
        return NULL;
	}

#ifdef __APPLE__
    size_t usableSize = malloc_size(mdctx);
#elif __linux__
    size_t usableSize = malloc_usable_size(mdctx);
#endif

    jobject result = (*env)->NewDirectByteBuffer(env, mdctx, usableSize);
    if (result == NULL) {
        throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not NewDirectByteBuffer()");
    }

    return result;
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl4j_OpenSSLMessageDigestNative_nativeUpdateWithByte
    (JNIEnv *env, jobject obj, jobject context, jbyte byteData) {
      EVP_MD_CTX* context_data = get_context_from(env, context);
      if (context_data != NULL) {
  	    if (1 != EVP_DigestUpdate(context_data, &byteData, 1)) {
             throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestUpdate failed");
  	    }
      }
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl4j_OpenSSLMessageDigestNative_nativeUpdateWithByteArray
  (JNIEnv *env, jobject obj, jobject context, jbyteArray jarray, jint offset, jint length) {
    if (jarray == NULL) {
        throwErrorWithOpenSSLInternalError(env, NULL_POINTER_EXCEPTION, "array is NULL");
        return;
    }

    EVP_MD_CTX* context_data = get_context_from(env, context);
    if (context_data != NULL) {
        jboolean isCopy = JNI_FALSE;

        /* TODO this copies the whole array, even if length is 1 byte */
        jbyte *carray = (*env)->GetByteArrayElements(env, jarray, &isCopy);
        if (carray != NULL) {
    	    if (1 != EVP_DigestUpdate(context_data, carray + offset, length)) {
               throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestUpdate failed");
	        }
            /* JNI_ABORT: Don't copy back the array, nothing has changed */
            (*env)->ReleaseByteArrayElements(env, jarray, carray, JNI_ABORT);
        } else {
            throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "GetByteArrayElements for array failed");
        }
    }
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl4j_OpenSSLMessageDigestNative_nativeUpdateWithByteBuffer
  (JNIEnv *env, jobject obj, jobject context, jobject bb, jint offset, jint length) {
    if (bb == NULL) {
        throwErrorWithOpenSSLInternalError(env, NULL_POINTER_EXCEPTION, "ByteBuffer is NULL");
        return;
    }
    EVP_MD_CTX* context_data = get_context_from(env, context);
    if (context_data != NULL) {
        jbyte* buffer = (*env)->GetDirectBufferAddress(env, bb);
        if (buffer != NULL) {
            jbyte* offset_buffer = buffer + offset;

    	    if (1 != EVP_DigestUpdate(context_data, offset_buffer, length)) {
               throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestUpdate failed");
	        }
        } else {
            throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "GetDirectBufferAddress for ByteBuffer failed");
        }
    }
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl4j_OpenSSLMessageDigestNative_nativeFinal
  (JNIEnv *env, jobject obj, jobject context, jbyteArray jdigest) {
    EVP_MD_CTX* context_data = get_context_from(env, context);
    if (jdigest == NULL) {
        throwErrorWithOpenSSLInternalError(env, NULL_POINTER_EXCEPTION, "Digest array is NULL");
        return;
    }
    if (context_data != NULL) {
        jbyte cdigest[EVP_MAX_MD_SIZE];
        unsigned int actualSize;
  	    if (1 != EVP_DigestFinal_ex(context_data, (unsigned char*)cdigest, &actualSize)) {
            throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestFinal_ex failed");
	    }
        (*env)->SetByteArrayRegion(env, jdigest, 0, actualSize, cdigest);
    }
}

JNIEXPORT void JNICALL Java_de_sfuhrm_openssl4j_OpenSSLMessageDigestNative_nativeInit
  (JNIEnv *env, jobject obj, jobject context, jstring jalgoName) {
    if (jalgoName == NULL) {
        throwErrorWithOpenSSLInternalError(env, NULL_POINTER_EXCEPTION, "Algorithm name is NULL");
        return;
    }
    EVP_MD_CTX* context_data = get_context_from(env, context);
    if (context_data == NULL) {
        throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestInit_ex failed");
        return;
     }

    jsize nameLength = (*env)->GetStringUTFLength(env, jalgoName);

    char javaNameC[256];
    if (nameLength > sizeof(javaNameC)) {
        throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Algorithm name exceeds the limit");
        return;
    }

    jboolean isCopy;
    const char * cstr = (*env)->GetStringUTFChars(env, jalgoName, &isCopy);
    strncpy(javaNameC, cstr, sizeof(javaNameC));
    (*env)->ReleaseStringUTFChars(env, jalgoName, cstr);

    const EVP_MD *evp_md = EVP_get_digestbyname(javaNameC);
    if (evp_md == NULL) {
        throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Named MessageDigest was not found");
        return;
    }

    if (1 != EVP_DigestInit_ex(context_data, evp_md, NULL)) {
        throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestInit_ex failed");
        return;
    }
}
