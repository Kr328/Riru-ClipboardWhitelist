#include <stdio.h>
#include <jni.h>
#include <dlfcn.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>

#include "hook.h"
#include "log.h"
#include "utils.h"
#include "inject.h"

#define EXPORT __attribute__((visibility("default")))

#define DEX_PATH  "/system/framework/boot-clipboard-whitelist.jar"

EXPORT
int nativeForkSystemServerPost(JNIEnv *env, jclass clazz, jint res) {
    if (res != 0)
        return 0;

    invoke_inject_method(env, NULL);

    return 0;
}

EXPORT
void onModuleLoaded() {
    char buffer[4096];
    char *p = NULL;

    strcpy(buffer,(p = getenv("CLASSPATH")) ? p : "");
    strcat(buffer,":" DEX_PATH);
    setenv("CLASSPATH",buffer,1);

    hook_install(&find_inject_class_method);
}

