/*
** OpenSSL Cipher to Java Binding Code
** See here for an example of the EVP API:
** https://wiki.openssl.org/index.php/EVP_Symmetric_Encryption_and_Decryption
** @author Stephan Fuhrmann
** @author Daniel Thertell
*/

#include <stdlib.h>
#include <string.h>
#include <openssl/err.h>
#include <stdio.h>
#include <openssl/core.h>

#ifdef __APPLE__
#include <malloc/malloc.h>
#elif __linux__
#include <malloc.h>
#endif

#include "openssl4j.h"
#include "openssl4j_cipher.h"

#include "de_sfuhrm_openssl4j_OpenSSLCipherNative.h"

/* Callback for EVP_CIPHER_do_all that counts the number of CIPHER algorithms. */
static void EVP_CIPHER_do_all_count_func(const EVP_CIPHER *ciph, const char *from, const char *to, void *x) {
    if (ciph != NULL) {
        jint *numOfAlgos = (jint*)x;
        (*numOfAlgos)++;
    }
}

/* Callback for EVP_CIPHER_do_all that sets the string array elements.
** @param ciph cipher, can be NULL if this is an alias.
** @param from the name of the algorithm.
** @param to NULL if this is not an alias, or the target EVP_MD if this is a an alias.
** @param x the last param passed to the EVP_MD_do_all() call.
*/
static void EVP_CIPHER_do_all_string_array_set(const EVP_CIPHER *ciph, const char *from, const char *to, void *x) {
    struct StringArrayPosition *sap = (struct StringArrayPosition*)x;
    if (ciph == NULL) {
        // alias
        return;
    }
    const char *evp_name = EVP_CIPHER_name(ciph);

    jstring algoNameString = (*sap->env)->NewStringUTF(sap->env, evp_name);
    if (algoNameString == NULL) {
        return;
    }

    (*sap->env)->SetObjectArrayElement(sap->env, sap->array, sap->index, algoNameString);
    sap->index++;
}

// Looks up cipher by name, corrects _ to - and attempts to use the EVP lookup first, 
// falling back to the EVP cipher functions if the EVP lookup fails. 
EVP_CIPHER* GetCipher(const char* OriginalName, long libCtx) {
    
    char FixedName[strlen(OriginalName)];

    strcpy(FixedName, OriginalName);
    
    for(int i = 0; i < strlen(FixedName); i++) {
        if(FixedName[i] == '_') {
            FixedName[i] = '-';
        }
    }

    EVP_CIPHER* cipher = NULL;
    if(libCtx != 0) {
        struct OpenSSLProviderHolder* handle = (struct OpenSSLProviderHolder*)libCtx;
        cipher = EVP_CIPHER_fetch(handle->lib, OriginalName, "-default");
    }else {
        cipher = EVP_CIPHER_fetch(NULL, OriginalName, "-default");
    }

    if(cipher == NULL) {
        if(strcmp(FixedName, "AES-256-GCM") == 0) {
            return (EVP_CIPHER*)EVP_aes_256_gcm();
        }else if (strcmp(FixedName, "AES-256-CTR") == 0) {
            return (EVP_CIPHER*)EVP_aes_256_ctr();
        }else if (strcmp(FixedName, "AES-256-CBC") == 0) {
            return (EVP_CIPHER*)EVP_aes_256_cbc();
        }else if (strcmp(FixedName, "AES-128-CBC") == 0) {
            return (EVP_CIPHER*)EVP_aes_128_cbc();
        }else if (strcmp(FixedName, "AES-256-ECB") == 0) {
            return (EVP_CIPHER*)EVP_aes_256_ecb();
        }else if (strcmp(FixedName, "AES-128-ECB") == 0) {
            return (EVP_CIPHER*)EVP_aes_128_ecb();
        }
    }

    return cipher;  
}

JNIEXPORT jobjectArray JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCipherNative_listCiphers
  (JNIEnv *env, jclass clazz) {
  struct StringArrayPosition sap;
  jobjectArray result = NULL;

  sap.index = 0;
  sap.length = 0;
  sap.env = env;
  sap.array = NULL;

  EVP_CIPHER_do_all(EVP_CIPHER_do_all_count_func, &sap.length);
  jclass stringClass = (*env)->FindClass(env, "java/lang/String");
  if (stringClass == NULL) {
    return NULL;
  }

  result = (*env)->NewObjectArray(env, sap.length, stringClass, NULL);
  sap.array = result;

  EVP_CIPHER_do_all(EVP_CIPHER_do_all_string_array_set, &sap);

  return result;
}

// Creates a context for the cipher.
JNIEXPORT jobject JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCipherNative_nativeContext
  (JNIEnv *env, jobject obj) {
    EVP_CIPHER_CTX *cipherctx;

	if ((cipherctx = EVP_CIPHER_CTX_new()) == NULL) {
        throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not allocate context");
        return NULL;
	}

#ifdef __APPLE__
    size_t usableSize = malloc_size(cipherctx);
#elif __linux__
    size_t usableSize = malloc_usable_size(cipherctx);
#endif

    jobject result = (*env)->NewDirectByteBuffer(env, cipherctx, usableSize);
    if (result == NULL) {
        throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not NewDirectByteBuffer()");
    }

    return result;
}

// This will clean up the cipher context when called. 
// The JVM will only call this when it runs low on RAM or has extra CPU cycles that can be spent on GC.
// As a result, this will be flagged as a memory "leak" by Valgrind most of the time, but it is a false positive.
JNIEXPORT void JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCipherNative_removeContext
  (JNIEnv *env, jclass clazz, jobject context) {
    EVP_CIPHER_CTX *cipherctx = get_context_from(env, context);
    if (cipherctx != NULL) {
        EVP_CIPHER_CTX_free(cipherctx);
    }
}

// Looks up a cipher and sets its IV and key for the provided cipher context. Uses the provided lib context.
int initCipher(const char* cName,const unsigned char* cKey,int cKeyLen, unsigned char * cIV, int cIVLen, EVP_CIPHER_CTX * cipherctx, long libCtxInt, int encrypt) {
    EVP_CIPHER* cipher = GetCipher(cName, libCtxInt);

    if(cipher == NULL) {
        return -1;
    }

    jint res;

    // GCM requires a special pre-init sequence to set the IV len.
    if(strstr(cName, "gcm") != NULL || strstr(cName, "GCM") != NULL || strstr(cName, "ctr") != NULL || strstr(cName, "CTR") != NULL) {
        OSSL_PARAM params[] = {
            { NULL, 0, NULL, 0, 0 },
            { NULL, 0, NULL, 0, 0 }
        }; 

        OSSL_PARAM ivLenParam;
        ivLenParam.key = "ivlen";
        ivLenParam.data_type = OSSL_PARAM_UNSIGNED_INTEGER;
        ivLenParam.data = &cIVLen;
        ivLenParam.data_size = 1;
        ivLenParam.return_size = 0;

        params[0] = ivLenParam;

        res = EVP_CipherInit_ex2(cipherctx, cipher, NULL, NULL, !!encrypt, params); // Re-init with key and iv = 0. params will be called automaticly after pre init.

        if(res == 0) {
            return -3;
        }

        res = EVP_CipherInit_ex2(cipherctx, NULL , cKey, (const unsigned char*)cIV, !!encrypt, NULL); // Post init now that IV len is set. cipher should be NULL for this call.
    }else {
        res = EVP_CipherInit_ex2(cipherctx, cipher, cKey, (const unsigned char*)cIV, !!encrypt, NULL); // !! forces encrypt to be a 1 or 0.
    }

    EVP_CIPHER_free(cipher);

    return res;
}

// Creates a new cipher that can be used by the JVM.
// The JINT that is returned indicates success or failure.
// The context is a pointer to a cipher that must already be created.
JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCipherNative_engineInitNative
  (JNIEnv *env, jclass clazz, jobject context, jstring name, jbyteArray key, jbyteArray iv, jint encrypt, jlong libCtxInt) {
    EVP_CIPHER_CTX *cipherctx = get_context_from(env, context);

    if(cipherctx == NULL) {
        throwErrorWithOpenSSLInternalError(env, UNSUPPORTED_OPERATION_EXCEPTION, "Cipher context is null for engine init.");
    }

    const char* cName = (*env)->GetStringUTFChars(env, name, 0);
    const unsigned char* cKey = (const unsigned char*)((*env)->GetByteArrayElements(env, key, 0));
    unsigned char* cIV = (unsigned char*)((*env)->GetByteArrayElements(env, iv, 0));
    int cKeyLen = (*env)->GetArrayLength(env, key);
    unsigned int cIVLen = (*env)->GetArrayLength(env, iv);

    int res = initCipher(cName, cKey, cKeyLen, cIV, cIVLen, cipherctx, libCtxInt, encrypt);

    switch(res) {
        case -1: {
            int msgLen = strlen("Failed to load cipher:  ") + strlen(cName) + 2;
            char ErrorMessage[msgLen];

            snprintf(ErrorMessage, msgLen, "Failed to load cipher: %s", cName);

            throwErrorWithOpenSSLInternalError(env, UNSUPPORTED_OPERATION_EXCEPTION, ErrorMessage);

            break;
        }
        case -2: {
            int msgLen = strlen("Provided key length of 000000 does not match the expected key length for the requested cipher. ") + 1;
            char ErrorMessage[msgLen];

            snprintf(ErrorMessage, msgLen, "Provided key length of %d does not match the expected key length for the requested cipher. ", cKeyLen);

            throwErrorWithOpenSSLInternalError(env, UNSUPPORTED_OPERATION_EXCEPTION, ErrorMessage);

            break;
        }
        case -3: {
            int msgLen = strlen("Error running engine int native. Error pre-initilizing for GCM. IV len: 0000 ") + 1;

            char ErrorMessage[msgLen];

            snprintf(ErrorMessage, msgLen, "Error running engine int native. Error pre-initilizing for GCM. IV len: %d", cIVLen);
            throwErrorWithOpenSSLInternalError(env, UNSUPPORTED_OPERATION_EXCEPTION, ErrorMessage);
            break;
        }
        case 0: {
            int msgLen = strlen("Error running engine int native. GCM: 0 IV len: 0000 ") + 1;

            char ErrorMessage[msgLen];

            snprintf(ErrorMessage, msgLen, "Error running engine int native. GCM: %d IV len: %d", strstr(cName, "gcm") != NULL || strstr(cName, "GCM") != NULL, cIVLen);
            throwErrorWithOpenSSLInternalError(env, UNSUPPORTED_OPERATION_EXCEPTION, ErrorMessage);
            break;
        }
    }

    (*env)->ReleaseByteArrayElements(env, key, (jbyte*)cKey, 0);
    (*env)->ReleaseStringUTFChars(env, name, (const char*)cName);
    (*env)->ReleaseByteArrayElements(env, iv, (jbyte*)cIV, 0);

    return res;
}

// Attempts to get the native block size for a given cipher via name. Uses native OpenSSL functions.
JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCipherNative_getBlockSizeNative(JNIEnv *env, jclass clazz,  jobject context, jstring name, jlong libCtxInt) {
    ERR_clear_error();
    const char* cName = (*env)->GetStringUTFChars(env, name, 0);

    EVP_CIPHER* cipher = GetCipher(cName, libCtxInt);

    (*env)->ReleaseStringUTFChars(env, name, (const char*)cName);

    if(cipher == NULL) {
        char ErrorMessage[strlen("Request for  returned null. using libCtx: ") + strlen(cName) + 6];
        strcpy(ErrorMessage, "Request for ");
        strcat(ErrorMessage, cName);
        strcat(ErrorMessage, " returned null. using libCtx: ");

        if(libCtxInt != 0) {
            strcat(ErrorMessage, "true");
        }else {
            strcat(ErrorMessage, "false");
        }

        throwErrorWithOpenSSLInternalError(env, NULL_POINTER_EXCEPTION, ErrorMessage);
        return -1;
    }

    jint size = EVP_CIPHER_get_block_size(cipher);

    EVP_CIPHER_free(cipher);

    return size;
}

// Attempts to get the IV originaly used to initilize a given cipher context.
JNIEXPORT jbyteArray JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCipherNative_getOriginalIVNative(JNIEnv *env, jclass clazz,  jobject context) {
    EVP_CIPHER_CTX *cipherctx = get_context_from(env, context);
   int cipherLen = EVP_CIPHER_CTX_get_iv_length(cipherctx);
   char* buf = (char*)malloc(cipherLen);

    if(buf == NULL) {
        return NULL;
    }

   int res = EVP_CIPHER_CTX_get_original_iv(cipherctx, buf, cipherLen);

    if(res == 0) {
        return NULL;
    }

   jbyteArray IvArr = (*env)->NewByteArray(env, cipherLen);

   jbyte *IvArrPtr = (*env)->GetByteArrayElements(env, IvArr, NULL);

   for(int i = 0; i < cipherLen; i++) {
       IvArrPtr[i] = buf[i];
   }

   (*env)->ReleaseByteArrayElements(env, IvArr, IvArrPtr, 0);

   free(buf);

   return IvArr;
}

// Calls the update function as part of an encryption or decryption operation. 
int opensslUpdate(EVP_CIPHER_CTX *cipherctx, const unsigned char * bytesIn, unsigned char * bytesOut, int inLen, int* outLen, int encrypt) {
     int resp = 1;

    if(encrypt) {
        resp = EVP_EncryptUpdate(cipherctx, bytesOut, outLen, bytesIn, inLen);
    }else {
        resp = EVP_DecryptUpdate(cipherctx, bytesOut, outLen, bytesIn, inLen);
    }

   return resp;
}

// JNI function to pass in bytes for encryption or decryption.
JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCipherNative_updateNative(JNIEnv *env, jclass clazz,  jobject context, jbyteArray out, jbyteArray in, jint inlen, jint encrypt, jint inputOffset) {
    EVP_CIPHER_CTX *cipherctx = get_context_from(env, context);

    const unsigned char *bytesIn = (const unsigned char*)((*env)->GetByteArrayElements(env, in, 0));
    bytesIn += inputOffset;

    unsigned char *bytesOut = (unsigned char*)(*env)->GetByteArrayElements(env, out, 0);

    int outlen = 0;

    int res = opensslUpdate(cipherctx, bytesIn, bytesOut, inlen, &outlen, encrypt);

    bytesIn -= inputOffset;// Reset offset before release.

   (*env)->ReleaseByteArrayElements(env, in, (jbyte*)bytesIn, 0);
   (*env)->ReleaseByteArrayElements(env, out, (jbyte*)bytesOut, 0);

    if(res != 0) {
        return outlen;
    }

    throwErrorWithOpenSSLInternalError(env, UNSUPPORTED_OPERATION_EXCEPTION, "Error running update native");

   return -1;
}

// Attempts to finalize the encryption or decryption, writing any remaining bytes out to the output buffer.
int opensslDoFinal(EVP_CIPHER_CTX *cipherctx, unsigned char* bytesOut, int outputOffset, int* outputLen, int encrypt) {
    int resp = 1;

    if(encrypt) {
        resp = EVP_EncryptFinal_ex(cipherctx, bytesOut + outputOffset, outputLen);
    }else {
        resp = EVP_DecryptFinal_ex(cipherctx, bytesOut + outputOffset, outputLen);
    }

    return resp;
}

// JNI interface for finalizing an encryption or decryption operation.
JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCipherNative_doFinalNative(JNIEnv *env, jclass clazz,  jobject context, jbyteArray out, jint encrypt, jint outputOffset) {
    EVP_CIPHER_CTX *cipherctx = get_context_from(env, context);
    unsigned char *bytesOut = (unsigned char*)(*env)->GetByteArrayElements(env, out, 0);

    int resp = 1, outLength = 0;

    resp = opensslDoFinal(cipherctx, bytesOut, outputOffset, &outLength, encrypt);

   (*env)->ReleaseByteArrayElements(env, out, (jbyte*)bytesOut, 0);

    if(resp != 0) {
        return outLength; // outLength is set to the numebr of bytes written. Return this instead of the 0 (fail) or 1 (pass) when resp == 1.
    }

    char ErrorMessage[strlen("Error running dofinal. Encrypting: ") + 6];

    strcpy(ErrorMessage, "Error running dofinal. Encrypting: ");

    if(encrypt) {
        strcat(ErrorMessage, "true");
    }else {
        strcat(ErrorMessage, "false");
    }

    throwErrorWithOpenSSLInternalError(env, UNSUPPORTED_OPERATION_EXCEPTION, ErrorMessage);

   return -1;
}

// JNI method for setting a special GCM tag that is required for GCM decryption.
JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCipherNative_setGCMTagNative(JNIEnv *env, jclass clazz,  jobject context, jbyteArray key) {
    EVP_CIPHER_CTX *cipherctx = get_context_from(env, context);
    unsigned char *cKey = (unsigned char*)(*env)->GetByteArrayElements(env, key, 0);
    int cKeyLen = (*env)->GetArrayLength(env, key);

    OSSL_PARAM params[] = {
        { NULL, 0, NULL, 0, 0 },
        { NULL, 0, NULL, 0, 0 }
    }; 
    // This is an easy way to initialize an array that is two params long, without calling malloc.
    // This way, it is cleaned up when it goes out of scope.
    // A null param indicates the end of the params array.
    // A few lines below, we overwrite the first element in the array with a struct that contains the correct values.

    OSSL_PARAM keyParam;
    keyParam.key = "tag";
    keyParam.data_type = OSSL_PARAM_OCTET_STRING;
    keyParam.data = cKey;
    keyParam.data_size = cKeyLen;
    keyParam.return_size = 0;

    params[0] = keyParam;

    int res = EVP_CIPHER_CTX_set_params(cipherctx, params);

    (*env)->ReleaseByteArrayElements(env, key, (jbyte*)cKey, 0);

    return res;
}

// JNI method for reading a special tag used by GCM encryption.
JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCipherNative_getGCMTagNative(JNIEnv *env, jclass clazz,  jobject context, jbyteArray key) {
    EVP_CIPHER_CTX *cipherctx = get_context_from(env, context);
    unsigned char *cKey = (unsigned char*)(*env)->GetByteArrayElements(env, key, 0);
    int cKeyLen = (*env)->GetArrayLength(env, key);

    OSSL_PARAM params[] = {
        { NULL, 0, NULL, 0, 0 },
        { NULL, 0, NULL, 0, 0 }
    }; 
    // This is an easy way to initialize an array that is two params long, without calling malloc.
    // This way, it is cleaned up when it goes out of scope.
    // A null param indicates the end of the params array.
    // A few lines below, we overwrite the first element in the array with a struct that contains the correct values.

    OSSL_PARAM keyParam;
    keyParam.key = "tag";
    keyParam.data_type = OSSL_PARAM_OCTET_STRING;
    keyParam.data = cKey;
    keyParam.data_size = cKeyLen;
    keyParam.return_size = 0;

    params[0] = keyParam;

    int res = EVP_CIPHER_CTX_get_params(cipherctx, params);

    (*env)->ReleaseByteArrayElements(env, key, (jbyte*)cKey, 0);

    if(res >= 0) {
        return keyParam.return_size;
    }

    return res;
}
