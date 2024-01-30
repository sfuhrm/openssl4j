/*
** OpenSSL Common Java Binding Code
** @author Stephan Fuhrmann
** @author Daniel Thertell
*/

#include <jni.h>
#include "openssl4j.h"
#include <openssl/err.h>
#include <stdlib.h>
#include <string.h>
#include <openssl/evp.h>


void throw_error(JNIEnv *env, const char *exceptionClassName, const char *message) {
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

void* get_context_from(JNIEnv *env, jobject context) {
    if (context == NULL) {
        throw_error(env, NULL_POINTER_EXCEPTION, "context is NULL");
        return NULL;
    }
    void* context_data = (void*) (*env)->GetDirectBufferAddress(env, context);
    if (context_data == NULL) {
        throw_error(env, ILLEGAL_STATE_EXCEPTION, "GetDirectBufferAddress() for Context failed");
    }
    return context_data;
}

// Recursive function that pops all errors off of the OpenSSL errorstack and gets each message, appending to the previous message (if any).
char * getAllErrorMessages(char* previousMessage, const char* delimiter) {
    unsigned long errCode = ERR_get_error();

    if(errCode == 0) {
        if(previousMessage == NULL) {
            return "No Errors"; // Return a default value if no previous message was set, and no errors were found.
        }
        return previousMessage;
    }

    char* newMessage = ERR_error_string(errCode, NULL);
    size_t totalLen = strlen(newMessage);

    if(previousMessage != NULL) {
        totalLen += strlen(previousMessage) + strlen(delimiter);
    }

    char* newFullString = malloc(totalLen+1);

    if(previousMessage != NULL) {
        strcpy(newFullString, previousMessage);
        free((void *)previousMessage);
        strcat(newFullString, delimiter);
        strcat(newFullString, newMessage);
    }else {
        strcpy(newFullString, newMessage);
    }

    return getAllErrorMessages(newFullString, delimiter);
}

// Throws a Java error that contains all errors that are on the OpenSSL error stack, and clears the error stack. 
void throwErrorWithOpenSSLInternalError(JNIEnv *env, const char* errorClass, const char *message) {
  char* internalMessage = getAllErrorMessages(NULL, ", \n");
  char ErrorMessage[strlen(message) + strlen(" internal error: \n") + strlen(internalMessage) + 1];
  strcpy(ErrorMessage, message);

  strcat(ErrorMessage, " internal error: ");
  strcat(ErrorMessage, internalMessage);

  throw_error(env, errorClass, ErrorMessage);
  
  if(strcmp(internalMessage, "No Errors") != 0) {
    free(internalMessage);
  }
}

EVP_MD* GetMessageDigest(const char* OriginalName, long libCtx) {
    if(libCtx != 0) {
        struct OpenSSLProviderHolder* handle = (struct OpenSSLProviderHolder*)libCtx;
        return EVP_MD_fetch(handle->lib, OriginalName, NULL);
    }else {
        return EVP_MD_fetch(NULL, OriginalName, NULL);
    }
}