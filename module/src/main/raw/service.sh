#!/system/bin/sh

MODDIR=${0%/*}

if [ -f "$MODDIR/apk_installed" ];then
  exit 0
fi

while sleep 10
do
    if [ -d "/sdcard/Android" ] && [ "$(getprop sys.boot_completed)" = "1" ];then
        break
    fi
done

touch "$MODDIR/apk_installed"

/system/bin/am start -n com.github.kr328.clipboard/.InstalledActivity