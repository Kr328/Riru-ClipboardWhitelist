# Restore data directory permissions

ui_print "- Restore data permissions"
set_perm_recursive "$MODULE_DATA_PATH" 1000 1000 0700 0600 u:object_r:system_data_file:s0