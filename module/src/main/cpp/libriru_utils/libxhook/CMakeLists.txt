cmake_minimum_required(VERSION 3.0)
project(xhook)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_C_STANDARD   11)

add_library(xhook STATIC xhook.c xh_core.c xh_elf.c xh_jni.c xh_log.c xh_util.c xh_version.c)
target_link_libraries(xhook log dl)
