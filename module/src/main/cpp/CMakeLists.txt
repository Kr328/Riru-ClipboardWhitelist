cmake_minimum_required(VERSION 3.0)
project(riru_ibr)

include_directories(libriru_utils)
include_directories(libriru_utils/libriru)
include_directories(libriru_utils/libxhook)

add_subdirectory(libriru_utils)

add_library(riru_clipboard_whitelist SHARED main.c hook.c inject.c utils.c)
target_link_libraries(riru_clipboard_whitelist riru_utils log)

