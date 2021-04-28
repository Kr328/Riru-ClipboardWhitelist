SKIPUNZIP=1

# extract verify.sh
ui_print "- Extracting verify.sh"
unzip -o "$ZIPFILE" 'verify.sh' -d "$TMPDIR" >&2
if [ ! -f "$TMPDIR/verify.sh" ]; then
  ui_print    "*********************************************************"
  ui_print    "! Unable to extract verify.sh!"
  ui_print    "! This zip may be corrupted, please try downloading again"
  abort "*********************************************************"
fi
. $TMPDIR/verify.sh

# extract riru.sh
extract "$ZIPFILE" 'riru.sh' "$MODPATH"
. $MODPATH/riru.sh

enforce_install_from_magisk_app
check_riru_version
check_architecture
check_sdk_version

# extract libs
ui_print "- Extracting module files"

extract "$ZIPFILE" 'module.prop' "$MODPATH"
#extract "$ZIPFILE" 'post-fs-data.sh' "$MODPATH"
#extract "$ZIPFILE" 'uninstall.sh' "$MODPATH"
#extract "$ZIPFILE" 'sepolicy.rule' "$MODPATH"

mkdir "$MODPATH/riru"

if [ "$ARCH" = "x86" ] || [ "$ARCH" = "x64" ]; then
  ui_print "- Extracting x86 libraries"
  extract "$ZIPFILE" "riru_x86/lib/libriru_$RIRU_MODULE_ID.so" "$MODPATH"
  mv "$MODPATH/riru_x86/lib" "$MODPATH/riru/lib"

  if [ "$IS64BIT" = true ]; then
    ui_print "- Extracting x64 libraries"
    extract "$ZIPFILE" "riru_x86/lib64/libriru_$RIRU_MODULE_ID.so" "$MODPATH"
    mv "$MODPATH/riru_x86/lib64" "$MODPATH/riru/lib64"
  fi
  rmdir $MODPATH/riru_x86
else
  ui_print "- Extracting arm libraries"
  extract "$ZIPFILE" "riru/lib/libriru_$RIRU_MODULE_ID.so" "$MODPATH"

  if [ "$IS64BIT" = true ]; then
    ui_print "- Extracting arm64 libraries"
    extract "$ZIPFILE" "riru/lib64/libriru_$RIRU_MODULE_ID.so" "$MODPATH"
  fi
fi

# extract runtime dex & apk
ui_print "- Extracting dex & apk"
extract "$ZIPFILE" "runtime/runtime.dex" "$MODPATH"
extract "$ZIPFILE" "system/priv-app/ClipboardWhitelist/ClipboardWhitelist.apk" "$MODPATH"

# set permissions
ui_print "- Setting permissions"
set_perm_recursive "$MODPATH" 0 0 0755 0644
