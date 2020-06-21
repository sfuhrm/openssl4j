/*
** Generic OpenSLL to Java Binding Code
*/


JNIEXPORT jint JNICALL NATIVE_CONTEXT_SIZE
  (JNIEnv *env, jobject obj) {
    return sizeof(CONTEXT_T);
}

JNIEXPORT void JNICALL NATIVE_INIT
  (JNIEnv *env, jobject obj, jobject context) {
    CONTEXT_T* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        C_INIT_FUNC(context_data);
    }
}

JNIEXPORT void JNICALL NATIVE_UPDATE_BYTE
  (JNIEnv *env, jobject obj, jobject context, jbyte byteData) {
    CONTEXT_T* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        C_UPDATE_FUNC(context_data, &byteData, 1);
    }
}

JNIEXPORT void JNICALL NATIVE_UPDATE_BYTE_ARRAY
  (JNIEnv *env, jobject obj, jobject context, jbyteArray jarray, jint offset, jint length) {
    CONTEXT_T* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        jboolean isCopy = JNI_FALSE;

        /* TODO this copies the whole array, even if length is 1 byte */
        jbyte *carray = (*env)->GetByteArrayElements(env, jarray, &isCopy);
        if (carray != NULL) {
            C_UPDATE_FUNC(context_data, carray + offset, length);
            /* JNI_ABORT: Don't copy back the array, nothing has changed */
            (*env)->ReleaseByteArrayElements(env, jarray, carray, JNI_ABORT);
        } else {
            throw_error(env, ILLEGAL_STATE_EXCEPTION, "GetByteArrayElements for array failed");
        }
    }
}

JNIEXPORT void JNICALL NATIVE_UPDATE_BYTE_BUFFER
  (JNIEnv *env, jobject obj, jobject context, jobject bb, jint offset, jint length) {
    CONTEXT_T* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        jbyte* buffer = (*env)->GetDirectBufferAddress(env, bb);
        if (buffer != NULL) {
            jbyte* offset_buffer = buffer + offset;

            C_UPDATE_FUNC(context_data, offset_buffer, length);
        } else {
            throw_error(env, ILLEGAL_STATE_EXCEPTION, "GetDirectBufferAddress for ByteBuffer failed");
        }
    }
}

JNIEXPORT void JNICALL NATIVE_FINAL
  (JNIEnv *env, jobject obj, jobject context, jbyteArray jdigest) {
    CONTEXT_T* context_data = md_context_from(env, context);
    if (context_data != NULL) {
        jbyte cdigest[DIGEST_LENGTH];

        C_FINAL_FUNC((unsigned char*)cdigest, context_data);
        (*env)->SetByteArrayRegion(env, jdigest, 0, DIGEST_LENGTH, cdigest);
    }
}
