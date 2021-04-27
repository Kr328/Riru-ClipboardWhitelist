#include <stdio.h>
#include <jni.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <fcntl.h>
#include <errno.h>
#include <android/log.h>
#include <sys/stat.h>
#include <sys/system_properties.h>

#include "riru.h"

#define TAG "ClipboardWhitelist"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

#define EXPORT __attribute__((visibility("default"))) __attribute__((used))

#define RIRU_TARGET_API 25

#define INJECT_DEX_PATH "/runtime/runtime.dex"
#define INJECT_CLASS_NAME "com.github.kr328.clipboard.Injector"
#define INJECT_METHOD_NAME "inject"
#define INJECT_METHOD_SIGNATURE "(Ljava/lang/String;)V"

static void *dex;
static size_t dex_size;

static const char* magisk_path = NULL;

static int catch_exception(JNIEnv *env) {
    int result = (*env)->ExceptionCheck(env);

    // check status
    if (result) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
    }

    return result;
}

static int
load_and_invoke_dex(JNIEnv *env, void *dex_data, long dex_data_length, const char *argument) {
    // get system class loader
    jclass cClassLoader = (*env)->FindClass(env, "java/lang/ClassLoader");
    jmethodID mSystemClassLoader = (*env)->GetStaticMethodID(env, cClassLoader,
                                                             "getSystemClassLoader",
                                                             "()Ljava/lang/ClassLoader;");
    jobject oSystemClassLoader = (*env)->CallStaticObjectMethod(env, cClassLoader,
                                                                mSystemClassLoader);

    // load dex
    jobject bufferDex = (*env)->NewDirectByteBuffer(env, dex_data, dex_data_length);
    jclass cDexClassLoader = (*env)->FindClass(env, "dalvik/system/InMemoryDexClassLoader");
    jmethodID mDexClassLoaderInit = (*env)->GetMethodID(env, cDexClassLoader, "<init>",
                                                        "(Ljava/nio/ByteBuffer;Ljava/lang/ClassLoader;)V");
    jobject oDexClassLoader = (*env)->NewObject(env, cDexClassLoader,
                                                mDexClassLoaderInit,
                                                bufferDex,
                                                oSystemClassLoader);

    if (catch_exception(env)) return 1;

    // get loaded dex inject method
    jmethodID mFindClass = (*env)->GetMethodID(env, cDexClassLoader, "loadClass",
                                               "(Ljava/lang/String;Z)Ljava/lang/Class;");
    jstring sInjectClassName = (*env)->NewStringUTF(env, INJECT_CLASS_NAME);
    jclass cInject = (jclass) (*env)->CallObjectMethod(env, oDexClassLoader,
                                                       mFindClass, sInjectClassName, (jboolean) 0);

    if (catch_exception(env)) return 1;

    // find method
    jmethodID mLoaded = (*env)->GetStaticMethodID(env, cInject, INJECT_METHOD_NAME,
                                                  INJECT_METHOD_SIGNATURE);

    if (catch_exception(env)) return 1;

    // invoke inject method
    jstring stringArgument = (*env)->NewStringUTF(env, argument);

    (*env)->CallStaticVoidMethod(env, cInject, mLoaded, stringArgument);

    return catch_exception(env);
}

static void nativeForkSystemServerPost(JNIEnv *env, jclass clazz, jint res) {
    if (res == 0) {
        if (dex != NULL) {
            if (load_and_invoke_dex(env, dex, dex_size, "")) {
                LOGI("Inject dex failure");
            }
        }
    }
}

static void onModuleLoaded() {
    // called when the shared library of Riru core is loaded

    char path[PATH_MAX] = {'\0'};
    if (magisk_path == NULL) {
        LOGE("magisk path is null");
        return;
    }

    sprintf(path, "%s/%s", magisk_path, INJECT_DEX_PATH);

    int fd = open(path, O_RDONLY);
    if (fd < 0) {
        LOGE("open dex file: %s", strerror(errno));

        return;
    }

    struct stat stat;
    if (fstat(fd, &stat) < 0) {
        LOGE("get size of dex file: %s", strerror(errno));

        close(fd);

        return;
    }

    dex = malloc(stat.st_size);
    dex_size = stat.st_size;

    uint8_t *ptr = (uint8_t *) dex;
    int count = 0;

    while (count < stat.st_size) {
        int r = read(fd, ptr, stat.st_size - count);

        if (r < 0) {
            LOGE("read dex: %s", strerror(errno));

            free(dex);
            close(fd);

            dex = NULL;
            dex_size = 0;

            return;
        }

        count += r;
        ptr += r;
    }

    close(fd);
}

RiruVersionedModuleInfo module = {
        .moduleApiVersion = RIRU_TARGET_API,
        .moduleInfo = {
                .supportHide = true,
                .version = RIRU_MODULE_VERSION_CODE,
                .versionName = RIRU_MODULE_VERSION_NAME,
                .onModuleLoaded = onModuleLoaded,
                .forkAndSpecializePre = NULL,
                .forkAndSpecializePost = NULL,
                .forkSystemServerPre = NULL,
                .forkSystemServerPost = nativeForkSystemServerPost,
                .specializeAppProcessPre = NULL,
                .specializeAppProcessPost = NULL,
        }
};

EXPORT RiruVersionedModuleInfo *init(Riru *riru) {
    if (riru->riruApiVersion < RIRU_TARGET_API) return NULL;

    *riru->allowUnload = true;

    magisk_path = strdup(riru->magiskModulePath);

    return &module;
}