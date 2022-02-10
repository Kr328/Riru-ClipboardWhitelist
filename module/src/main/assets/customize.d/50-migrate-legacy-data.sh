# Migrate legacy data to ZygoteLoader data directory

if [ -f "/data/misc/clipboard/whitelist.list" ]; then
  ui_print "- Migrate legacy data directory"

  mkdir -p "$MODULE_DATA_PATH"
  cp -f "/data/misc/clipboard/whitelist.list" "$MODULE_DATA_PATH/whitelist.list"
  rm -rf "/data/misc/clipboard"
fi
