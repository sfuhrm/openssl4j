/*
** OpenSSL Cipher to Java Binding Code
** @author Stephan Fuhrmann
*/

#include <stdlib.h>
#include <string.h>
#include <openssl/evp.h>
#include <malloc.h>

#include "openssl4j.h"

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
