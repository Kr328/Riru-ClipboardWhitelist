cmake_minimum_required(VERSION 3.4.1)

if (NOT DEFINED RIRU_MODULE_ID)
    message(FATAL_ERROR "RIRU_MODULE_ID is not set")
endif ()

message("Build type: ${CMAKE_BUILD_TYPE}")

set(CMAKE_CXX_STANDARD 11)

set(LINKER_FLAGS "-ffixed-x18 -Wl,--hash-style=both")
set(C_FLAGS "-Werror=format -fdata-sections -ffunction-sections")

if (CMAKE_BUILD_TYPE STREQUAL "Release")
    set(C_FLAGS "${C_FLAGS} -O2 -fvisibility=hidden -fvisibility-inlines-hidden")
    set(LINKER_FLAGS "${LINKER_FLAGS} -Wl,-exclude-libs,ALL -Wl,--gc-sections")
else ()
    set(C_FLAGS "${C_FLAGS} -O0")
endif ()

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${C_FLAGS}")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${C_FLAGS}")

set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} ${LINKER_FLAGS}")
set(CMAKE_MODULE_LINKER_FLAGS "${CMAKE_MODULE_LINKER_FLAGS} ${LINKER_FLAGS}")

find_package(riru REQUIRED CONFIG)

add_definitions(
        "-DRIRU_API=${RIRU_API}"
        "-DRIRU_NAME=\"${RIRU_NAME}\""
        "-DRIRU_MODULE_ID=\"${RIRU_MODULE_ID}\""
        "-DRIRU_MODULE_VERSION_NAME=\"${RIRU_MODULE_VERSION_NAME}\""
        "-DRIRU_MODULE_VERSION_CODE=${RIRU_MODULE_VERSION_CODE}"
)

add_library(riru_${RIRU_MODULE_ID} SHARED main.c)
target_link_libraries(riru_${RIRU_MODULE_ID} log riru::riru)
set_target_properties(riru_${RIRU_MODULE_ID} PROPERTIES LINK_FLAGS_RELEASE -s)
