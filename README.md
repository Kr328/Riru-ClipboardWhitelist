# Riru - Clipboard Whitelist

A module of [Riru](https://github.com/RikkaApps/Riru)/[Zygisk](https://github.com/topjohnwu/zygisk-module-sample). Allowing apps to access the clipboard in the background on Android 10.

## Requirements

* [Riru](https://github.com/RikkaApps/Riru) >= 25.0 installed.
* Android 10+


## Feature

Allowing apps to access the clipboard in the background on Android 10.   


## Build

1. Install JDK ,Android SDK ,Android NDK

2. Configure local.properties 

   ```properties
   sdk.dir=/path/to/android/sdk
   ```

3. Configure keystore.properties

   ```properties
   keystore.path=/path/to/your/keystore.jks
   keystore.password=<keystore password>
   key.alias=<key alias>
   key.password=<key password>
   ```

4. Run build command 

    ``` bash 
    ./gradlew module:assembleRelease
    ```
    
4. Pick `riru-clipboard-whitelist-<version>.zip` from `module/build/outputs/magisk/<variant>`
