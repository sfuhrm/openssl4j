/*
** OpenSSL Common Java Binding Code
** @author Stephan Fuhrmann
*/

#include <jni.h>
#include "openssl4j.h"

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
