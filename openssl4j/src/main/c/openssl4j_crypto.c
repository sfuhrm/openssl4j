/*
** OpenSSL Cipher to Java Binding Code
** Contains helper functions to assist in setting up a library instance in FIPS mode
** @author Daniel Thertell
*/

#include <openssl/crypto.h>
#include <openssl/evp.h>
#include <openssl/err.h>
#include <string.h>
#include <stdint.h>
#include <openssl/pem.h>
#include <openssl/bio.h>
#include <openssl/encoder.h>

#include "openssl4j.h"

#include "de_sfuhrm_openssl4j_OpenSSLCryptoNative.h"

#if OPENSSL_VERSION_NUMBER >= 0x10101000L
#define OPENSSL_MD_NEW_FUNC EVP_MD_CTX_new
#define OPENSSL_MD_FREE_FUNC EVP_MD_CTX_free
#else
#define OPENSSL_MD_NEW_FUNC EVP_MD_CTX_create
#define OPENSSL_MD_FREE_FUNC EVP_MD_CTX_destroy
#endif

#define backupOSSLConf "/etc/ssl/ossl3/openssl.cnf"

char* opensslConfigLocation = NULL;

// Checks to make sure FIPS is enabled for the provided lib and the default lib.
JNIEXPORT jint JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCryptoNative_FIPSMode
  (JNIEnv *env, jclass obj, jlong handle) {
    uintptr_t temp = (uintptr_t) handle; //hide warnings about casting between int and pointer
    struct OpenSSLProviderHolder *ctx = (struct OpenSSLProviderHolder*) temp;
    return EVP_default_properties_is_fips_enabled(ctx->lib) && EVP_default_properties_is_fips_enabled(NULL);
}

// Sets the location of the OpenSSL config file.
void setOpensslConfigLocation(const char* source) {
  if(opensslConfigLocation != NULL) {
    free(opensslConfigLocation);
    opensslConfigLocation = NULL;
  }

  opensslConfigLocation = (char*)malloc(strlen(source) +1);

  if(opensslConfigLocation == NULL) {
    return;
  }

  strcpy(opensslConfigLocation, source);
}

// Creates a native lib instance and passes back a pointer to it.
unsigned long long createOpenSSLLibNative(int setFips, const char* confLocation, const char* libLocations, int *errCode) {
  ERR_clear_error();

  struct OpenSSLProviderHolder *handle = malloc(sizeof(struct OpenSSLProviderHolder)); // Create the pointer.

  setOpensslConfigLocation(confLocation);
  if(opensslConfigLocation == NULL) {
      free(handle);
      *errCode = 1;
      return 0;
  }

  int usedBackup = 0;

  if(strlen(opensslConfigLocation) < 3) {
    usedBackup++;
    if(OSSL_LIB_CTX_load_config(NULL, backupOSSLConf) == 0){
      free(handle);
      *errCode = 2;
      return 0;
    }
  }else {
    if(OSSL_LIB_CTX_load_config(NULL, opensslConfigLocation) == 0){
      free(handle);
      *errCode = 3;
      return 0;
    }
  }

  handle->lib = OSSL_LIB_CTX_get0_global_default();

  if(handle->lib == NULL) {
    free(handle);
    *errCode = 4;
    return 0;
  }

  if(usedBackup) {
    if(OSSL_LIB_CTX_load_config(handle->lib, backupOSSLConf) == 0){
      free(handle);
      *errCode = 5;
      return 0;
    }
  }else {
    if(OSSL_LIB_CTX_load_config(handle->lib, opensslConfigLocation) == 0){
      free(handle);
      *errCode = 6;
      return 0;
    }
  }

  // Set search path for provider libs (fips.so, default.so, etc.).
  if(OSSL_PROVIDER_set_default_search_path(handle->lib, libLocations) == 0) {
    free(handle);
    *errCode = 7;
    return 0;
  }

  // Load base provider.
  handle->baseProvider = OSSL_PROVIDER_load(handle->lib, "base");

  if (handle->baseProvider == NULL) {
    free(handle);
    *errCode = 8;
    return 0;
  }

  // Load legacy provider.
  handle->legacyProvider = OSSL_PROVIDER_load(handle->lib, "legacy");

  if (handle->legacyProvider == NULL) {
    free(handle);
    *errCode = 9;
    return 0;
  }

  // Load FIPS provider.
  handle->fipsProvider = OSSL_PROVIDER_load(handle->lib, "fips");


  if (handle->fipsProvider == NULL) {
    free(handle);
    *errCode = 10;
    return 0;
  }

  // Set FIPS for target lib and default lib. 
  // FIPS limits available algorithms in all of the loaded providers to those that are FIPS-compliant.
  if (!EVP_default_properties_enable_fips(handle->lib, setFips)) {
    free(handle);
    *errCode = 11;
    return 0;
  }

  if (!EVP_default_properties_enable_fips(NULL, setFips)) {
    free(handle);
    *errCode = 12;
    return 0;
  }


  return (uintptr_t)handle;
}

// Based on https://www.openssl.org/docs/man3.0/man7/fips_module.html
// JNI interface for creating the OpenSSL library.
JNIEXPORT jlong JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCryptoNative_CreateOpenSSLLibNative
  (JNIEnv *env, jclass obj, jint setFips, jstring config, jstring libDir) {

  if(setFips == 0) {
    return 0; // We don't support non-FIPS mode.
  }

    ERR_clear_error();


    const char* confLocation = (*env)->GetStringUTFChars(env, config, 0);
    const char* libLocations = (*env)->GetStringUTFChars(env, libDir, 0);

    int errCode = 0;
    jlong ptr = createOpenSSLLibNative(setFips, confLocation, libLocations, &errCode);
    
    (*env)->ReleaseStringUTFChars(env, config, confLocation);
    (*env)->ReleaseStringUTFChars(env, libDir, libLocations);

    // Error code switch, to help with debugging.
    switch (errCode)
    {
    case 1:
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Failed to save config path!");
      break;
    case 2:
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not load openssl backup config");
      break;
    case 3:
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not load openssl config");
      break;
    case 4:
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not get openssl library context");
      break;
    case 5:
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not load openssl backup config (part 2)");
      break;
    case 6:
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not load openssl config (part 2)");
      break;
    case 7:
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not set default search path for modules");
      break;
    case 8:
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not load openssl base provider");
      break;
    case 9:
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not load openssl legacy provider");
      break;
    case 10:
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not load openssl FIPS provider");
      break;
    case 11:
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not enable FIPS for openssl lib");
      break;
    case 12:
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Could not enable FIPS for default openssl lib");
      break;

    default:
      if(ptr == 0) {
        throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "Unknown error while creating lib instance");
      }
      break;
    }

    return ptr;
}


// Function to decrypt any private key and encode it into an expected format for the calling Java functions.
JNIEXPORT jbyteArray JNICALL Java_de_sfuhrm_openssl4j_OpenSSLCryptoNative_decryptPrivateKeyNative(JNIEnv *env, jclass clazz, jstring privateKey, jstring passPhrase) {

    const char* pKey = (*env)->GetStringUTFChars(env, privateKey, 0);
    const char* pass = (*env)->GetStringUTFChars(env, passPhrase, 0);

    int keyLen = (*env)->GetStringUTFLength(env, privateKey);

    BIO* pKeyMem = BIO_new_mem_buf(pKey, keyLen);

    EVP_PKEY* k = PEM_read_bio_PrivateKey(pKeyMem, NULL, NULL, (void *)pass);

    (*env)->ReleaseStringUTFChars(env, privateKey, (const char*)pKey);
    (*env)->ReleaseStringUTFChars(env, passPhrase, (const char*)pass);
    BIO_free(pKeyMem);

    OSSL_ENCODER_CTX *ectx;
    const char *format = "PEM";
    const char *structure = "PrivateKeyInfo";

    ectx = OSSL_ENCODER_CTX_new_for_pkey(k, OSSL_KEYMGMT_SELECT_KEYPAIR | OSSL_KEYMGMT_SELECT_DOMAIN_PARAMETERS, format, structure, NULL);

    if(ectx == NULL) {
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "unable to convert private key to unencrypted pem format, no encoders found");
      EVP_PKEY_free(k);
      return NULL;
    }

    unsigned char *data = NULL;
    size_t datalen;

    if(OSSL_ENCODER_to_data(ectx, &data, &datalen) == 0) {
      throwErrorWithOpenSSLInternalError(env, ILLEGAL_STATE_EXCEPTION, "unable to convert private key, failed to export key");
      EVP_PKEY_free(k);
      return NULL;
    }

    jbyteArray pKeyJava = (*env)->NewByteArray(env, datalen);

    jbyte *pKeyJavaPtr = (*env)->GetByteArrayElements(env, pKeyJava, NULL);

    for(int i = 0; i < datalen; i++) {
        pKeyJavaPtr[i] = data[i];
    }

    (*env)->ReleaseByteArrayElements(env, pKeyJava, pKeyJavaPtr, 0);
    OSSL_ENCODER_CTX_free(ectx);
    EVP_PKEY_free(k);

    OPENSSL_clear_free(data, datalen);

    return pKeyJava;
}
