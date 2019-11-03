//
// Created by Kr328 on 18-11-26.
//

#include "riru_utils.h"

#include "xhook.h"
#include "riru.h"

#include <jni.h>
#include <string.h>
#include <malloc.h>

//Public Export
int riru_utils_replace_native_functions(riru_utils_native_replace_t *functions, int length) {
    for ( int i = 0 ; i < length ; i++ ) {
        riru_utils_native_replace_t *current = functions + i;
        xhook_register(current->library_pattern ,current->symbol ,current->replace_function ,current->original_function);
    }

    if ( xhook_refresh(0) == 0 )
        xhook_clear();
    else
        return -1;

    for ( int i = 0 ; i < length ; i++ ) {
        riru_utils_native_replace_t *current = functions + i;

        void *replaced_original = riru_get_func(current->symbol);
        if ( replaced_original && replaced_original != *current->original_function ) {
            *current->original_function = replaced_original;
            riru_set_func(current->symbol ,current->replace_function);
        }
    }

    return 0;
}

//Public Export
int riru_utils_replace_jni_methods(riru_utils_jni_replace_method_t *classes ,int length ,JNIEnv *env) {
    for ( int i = 0 ; i < length ; i++ ) {
        riru_utils_jni_replace_method_t *current = classes + i;

        void *original_function = riru_get_native_method_func(current->class_name ,current->method_name ,current->signature);
        *current->original_function = original_function;
        riru_set_native_method_func(current->class_name ,current->method_name ,current->signature ,current->replace_function);

        JNINativeMethod method = {current->method_name ,current->signature ,current->replace_function};

        (*env)->RegisterNatives(env ,(*env)->FindClass(env ,current->class_name) ,&method ,1);
    }

    return 0;
}

int riru_utils_init_module(const char *module_name) {
	riru_set_module_name(module_name);

	return 0;
}
