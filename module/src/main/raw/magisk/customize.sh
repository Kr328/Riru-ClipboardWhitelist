#!/bin/sh

##########################################################################################
#
# Function Callbacks
#
# The following functions will be called by the installation framework.
# You do not have the ability to modify update-binary, the only way you can customize
# installation is through implementing these functions.
#
# When running your callbacks, the installation framework will make sure the Magisk
# internal busybox path is *PREPENDED* to PATH, so all common commands shall exist.
# Also, it will make sure /data, /system, and /vendor is properly mounted.
#
##########################################################################################
##########################################################################################
#
# The installation framework will export some variables and functions.
# You should use these variables and functions for installation.
#
# ! DO NOT use any Magisk internal paths as those are NOT public API.
# ! DO NOT use other functions in util_functions.sh as they are NOT public API.
# ! Non public APIs are not guranteed to maintain compatibility between releases.
#
# Available variables:
#
# MAGISK_VER (string): the version string of current installed Magisk
# MAGISK_VER_CODE (int): the version code of current installed Magisk
# BOOTMODE (bool): true if the module is currently installing in Magisk Manager
# MODPATH (path): the path where your module files should be installed
# TMPDIR (path): a place where you can temporarily store files
# ZIPFILE (path): your module's installation zip
# ARCH (string): the architecture of the device. Value is either arm, arm64, x86, or x64
# IS64BIT (bool): true if $ARCH is either arm64 or x64
# API (int): the API level (Android version) of the device
#
# Availible functions:
#
# ui_print <msg>
#     print <msg> to console
#     Avoid using 'echo' as it will not display in custom recovery's console
#
# abort <msg>
#     print error message <msg> to console and terminate installation
#     Avoid using 'exit' as it will skip the termination cleanup steps
#
# set_perm <target> <owner> <group> <permission> [context]
#     if [context] is empty, it will default to "u:object_r:system_file:s0"
#     this function is a shorthand for the following commands
#       chown owner.group target
#       chmod permission target
#       chcon context target
#
# set_perm_recursive <directory> <owner> <group> <dirpermission> <filepermission> [context]
#     if [context] is empty, it will default to "u:object_r:system_file:s0"
#     for all files in <directory>, it will call:
#       set_perm file owner group filepermission context
#     for all directories in <directory> (including itself), it will call:
#       set_perm dir owner group dirpermission context
#
##########################################################################################

RIRU_PATH="/data/misc/riru"
MODULE_NAME="clipboard_whitelist"
DATA_PATH="/data/misc/clipboard"
TARGET="$RIRU_PATH/modules/$MODULE_NAME"

# Check Riru Version
[[ ! -f "$RIRU_PATH/api_version" ]] && abort "! Please Install Riru - Core v19 or above"
VERSION=$(cat "$RIRU_PATH/api_version")
ui_print "- Riru API version is $VERSION"
[[ "$VERSION" -ge 4 ]] || abort "! Please Install Riru - Core v19 or above"

# Check Arch
if [[ "$ARCH" != "arm" && "$ARCH" != "arm64" ]]; then
  abort "! Unsupported platform: $ARCH"
else
  ui_print "- Device platform: $ARCH"
fi

# Check System API Level
if [[ "$API" -lt "29" ]];then
  ui_print "Unsupported api version ${API}"
  abort "This module only for Android 10+"
fi

# Remove 64-bit library
if [[ "$IS64BIT" = false ]]; then
  ui_print "- Removing 64-bit libraries"
  rm -rf "$MODPATH/system/lib64"
fi

# Setup Riru Module
[[ -d "$TARGET" ]] || mkdir -p "$TARGET" || abort "! Can't mkdir -p $TARGET"
rm -rf "$TARGET"
mv "$MODPATH/data" "$TARGET" || abort "! Can't setup riru module"
ui_print "- Riru setup"

# Create whitelist template
ui_print "- Create whitelist.list"
mkdir -p "$DATA_PATH"
touch "$DATA_PATH/whitelist.list"

# Set permission
ui_print "- Set permissions"
set_perm_recursive $MODPATH 0    0    0755 0644
set_perm_recursive $DATA_PATH  1000 1000 0700 0600 u:object_r:system_data_file:s0
