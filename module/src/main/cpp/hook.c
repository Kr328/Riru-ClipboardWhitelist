#include "hook.h"

#include <stdio.h>
#include <string.h>
#include <string.h>
#include <signal.h>

#define ELMLEN(a) (sizeof(a)/sizeof(*a))

// Patch Android Framework
static int android_runtime_start_reg_replaced(JNIEnv *env);
static int (*android_runtime_start_reg_original)(JNIEnv *env);

static void (*installed_on_register_jni_callback)(JNIEnv *env);

static riru_utils_native_replace_t native_replace_list[] = {
	{".*android_runtime.*" ,"_ZN7android14AndroidRuntime8startRegEP7_JNIEnv" ,(void*)&android_runtime_start_reg_replaced ,(void**)&android_runtime_start_reg_original}
};

int hook_install(void (*on_register_jni_callback)(JNIEnv *env)) {
    riru_utils_init_module("ifw_enhance");
	riru_utils_replace_native_functions(native_replace_list ,ELMLEN(native_replace_list));

	installed_on_register_jni_callback = on_register_jni_callback;

	return 0;
}

static int android_runtime_start_reg_replaced(JNIEnv *env) {
	int result = android_runtime_start_reg_original(env);

	installed_on_register_jni_callback(env);

	return result;
}
