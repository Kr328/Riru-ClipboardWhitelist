#pragma once

#include <jni.h>
#include <dlfcn.h>
#include <malloc.h>
#include <stdint.h>
#include <string.h>
#include <sys/mman.h>

#include "riru_utils.h"

#include "log.h"

int hook_install(void (*on_register_jni_callback)(JNIEnv *env));