#pragma once

#include <jni.h>

#define INJECT_CLASS_PATH "com/github/kr328/clipboard/Injector"
#define INJECT_METHOD_NAME "inject"

void find_inject_class_method(JNIEnv *env);
void invoke_inject_method(JNIEnv* env, const char *config_data);