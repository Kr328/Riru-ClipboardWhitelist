#include "inject.h"
#include "log.h"

static jclass inject_class;
static jmethodID inject_method;

void find_inject_class_method(JNIEnv *env) {
    inject_class = (*env)->FindClass(env ,INJECT_CLASS_PATH);
    inject_method = (*env)->GetStaticMethodID(env, inject_class ,INJECT_METHOD_NAME, "(Ljava/lang/String;)V");

    LOGI("inject_class=%p inject_method=%p", inject_class, inject_method);

    (*env)->ExceptionClear(env);
}

void invoke_inject_method(JNIEnv *env, const char *config_data) {
    if ( inject_method ) {
        (*env)->CallStaticVoidMethod(env, inject_class, inject_method, (*env)->NewStringUTF(env, config_data));
        (*env)->ExceptionClear(env);
    }
    else {
        LOGE("Failure to load dex");
    }
}
