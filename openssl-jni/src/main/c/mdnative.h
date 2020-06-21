/*
** Generic OpenSLL to Java Binding Code
*/


JNIEXPORT jint JNICALL NATIVE_CONTEX
JNIEXPORT void JNICALL NATIVE_INIT
  (JNIEnv *env, jobject obj, jobject context) {
    CONTEXT_T* context_data = md_context_from(env, context);
    if (context_data != NULL) {
    	if (1 != EVP_DigestInit_ex(mdctx, C_EVP_FUNC(), NULL)) {
           throw_error(env, ILLEGAL_STATE_EXCEPTION, "EVP_DigestInit_ex  failed");
    	}
    }
}
