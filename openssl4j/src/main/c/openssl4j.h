/*
** OpenSSL to Java Header Code
** @author Stephan Fuhrmann
** @author Daniel Thertell
*/

#ifndef OPENSSL4J_H
#define OPENSSL4J_H

#include <jni.h>
#include <openssl/provider.h>

#define NULL_POINTER_EXCEPTION "java/lang/NullPointerException"
#define ILLEGAL_STATE_EXCEPTION "java/lang/IllegalStateException"
#define UNSUPPORTED_OPERATION_EXCEPTION "java/lang/UnsupportedOperationException"


struct StringArrayPosition {
    /* The next write index in the array below. */
    jint index;
    /* The length of the array below. */
    jint length;
    /* The JNI env to use for doing JNI environment calls. */
    JNIEnv *env;
    /* The String array to store the names in. */
    jobjectArray array;
};

struct OpenSSLProviderHolder {
    OSSL_PROVIDER *baseProvider;
    OSSL_PROVIDER *legacyProvider;
    OSSL_PROVIDER *fipsProvider;
    OSSL_LIB_CTX *lib;
};

/*
* Throws an exception. Actually signals the JVM that an exception shall be thrown.
* C methods need to terminate normally.
* @param env the JNI environment.
* @param exceptionClassName the Java Name of the exception to throw, for example java/lang/NullPointerException.
* @param message the exception message to pass to the exception constructor.
*/
void throw_error(JNIEnv *env, const char *exceptionClassName, const char *message);


void throwErrorWithOpenSSLInternalError(JNIEnv *env, const char *exceptionClassName, const char *message);

/*
* Returns the MD / Crypto context from a passed in ByteBuffer jobject.
* @param env the JNI environment.
* @param context a pointer to the context ByteBuffer object.
*/
void* get_context_from(JNIEnv *env, jobject context);

EVP_MD* GetMessageDigest(const char* OriginalName, long libCtx);

unsigned long long createOpenSSLLibNative(int setFips, const char* confLocation, const char* libLocations, int *errCode);

#endif
