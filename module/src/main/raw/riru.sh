#!/sbin/sh

RIRU_MODULE_ID="%%%RIRU_MODULE_ID%%%"
RIRU_MIN_API_VERSION="%%%RIRU_MIN_API_VERSION%%%"
RIRU_MIN_VERSION_NAME="%%%RIRU_MIN_VERSION_NAME%%%"
RIRU_MIN_SDK_VERSION="%%%RURU_MIN_SDK_VERSION%%%"


if [ "$MAGISK_VER_CODE" -ge 21000 ]; then
  MAGISK_CURRENT_RIRU_MODULE_PATH=$(magisk --path)/.magisk/modules/riru-core
else
  MAGISK_CURRENT_RIRU_MODULE_PATH=/sbin/.magisk/modules/riru-core
fi

# This function will be used when util_functions.sh not exists
check_riru_version() {
  if [ ! -f "$MAGISK_CURRENT_RIRU_MODULE_PATH/api_version" ] && [ ! -f "/data/adb/riru/api_version" ] && [ ! -f "/data/adb/riru/api_version.new" ]; then
    ui_print "*********************************************************"
    ui_print "! Riru $RIRU_MODULE_MIN_RIRU_VERSION_NAME or above is required"
    ui_print "! Please install Riru from Magisk Manager or https://github.com/RikkaApps/Riru/releases"
    abort "*********************************************************"
  fi

  RIRU_API=$(cat "$MAGISK_CURRENT_RIRU_MODULE_PATH/api_version") || RIRU_API=$(cat "/data/adb/riru/api_version.new") || RIRU_API=$(cat "/data/adb/riru/api_version") || RIRU_API=0

  [ "$RIRU_API" -eq "$RIRU_API" ] || RIRU_API=0

  ui_print "- Riru API version: $RIRU_API"

  if [ "$RIRU_API" -lt $RIRU_MIN_API_VERSION ]; then
    ui_print "*********************************************************"
    ui_print "! Riru $RIRU_MIN_VERSION_NAME or above is required"
    ui_print "! Please upgrade Riru from Magisk Manager or https://github.com/RikkaApps/Riru/releases"
    abort    "*********************************************************"
  fi
}

check_sdk_version() {
  if [ "$API" -lt "$RIRU_MIN_SDK_VERSION" ];then
    abort "! Unsupported SDK version: $API < $RIRU_MIN_SDK_VERSION"
  else
    ui_print "- Android API level: $API"
  fi
}

check_architecture() {
  if [ "$ARCH" != "arm" ] && [ "$ARCH" != "arm64" ] && [ "$ARCH" != "x86" ] && [ "$ARCH" != "x64" ]; then
    abort "! Unsupported platform: $ARCH"
  else
    ui_print "- Device platform: $ARCH"
  fi
}

# This function will be used when util_functions.sh not exists
enforce_install_from_magisk_app() {
  if $BOOTMODE; then
    ui_print "- Installing from Magisk app"
  else
    ui_print "*********************************************************"
    ui_print "! Install from recovery is NOT supported"
    ui_print "! Some recovery has broken implementations, install with such recovery will finally cause Riru or Riru modules not working"
    ui_print "! Please install from Magisk app"
    abort    "*********************************************************"
  fi
}

if [ -f $MAGISK_CURRENT_RIRU_MODULE_PATH/util_functions.sh ]; then
  ui_print "- Load $MAGISK_CURRENT_RIRU_MODULE_PATH/util_functions.sh"
  # shellcheck disable=SC1090
  . $MAGISK_CURRENT_RIRU_MODULE_PATH/util_functions.sh
else
  if [ -f /data/adb/riru/util_functions.sh ]; then
    ui_print "- Load /data/adb/riru/util_functions.sh"
    . /data/adb/riru/util_functions.sh
  else
    abort "! Can't find /data/adb/riru/util_functions.sh"
  fi
fi