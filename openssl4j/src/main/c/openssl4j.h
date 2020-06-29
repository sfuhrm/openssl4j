/*
** OpenSSL to Java Header Code
** @author Stephan Fuhrmann
*/

#ifndef OPENSSL4J_H
#define OPENSSL4J_H

#include <jni.h>

#define NULL_POINTER_EXCEPTION "java/lang/NullPointerException"
#define ILLEGAL_STATE_EXCEPTION "java/lang/IllegalStateException"
#define UNSUPPORTED_OPERATION_EXCEPTION "java/lang/UnsupportedOperationException"

struct StringArrayPosition {
    jint index;
    jint length;
    JNIEnv *env;
    jobjectArray array;
};

void throw_error(JNIEnv *env, const char *exceptionClassName, const char *message);
void* get_context_from(JNIEnv *env, jobject context);

#endif
