#pragma once

#include <stdio.h>
#include <malloc.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>

char *malloc_and_load_file(const char *path);