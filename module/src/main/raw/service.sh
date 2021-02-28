#!/system/bin/sh

MODDIR=${0%/*}

LAUNCHED_PATH="$MODDIR/apk_launched"

if [ -f "$LAUNCHED_PATH" ];then
  exit 0
fi

while sleep 10
do
    if [ -d "/sdcard/Android" ] && [ "$(getprop sys.boot_completed)" = "1" ];then
        break
    fi
done

touch "$LAUNCHED_PATH"

/system/bin/am start -n com.github.kr328.clipboard/.InstalledActivity