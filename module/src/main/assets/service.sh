MODDIR=${0%/*}
FLAG_FILE="$MODDIR/.module_updated"

if [ ! -f "$FLAG_FILE" ]; then
  exit 0
fi

while [ ! -d "/sdcard/Android" ] || [ "$(getprop "sys.boot_completed")" != "1" ] ; do
  sleep 5
done

/system/bin/am start -n com.github.kr328.clipboard/.InstalledActivity

rm -rf "$FLAG_FILE"
