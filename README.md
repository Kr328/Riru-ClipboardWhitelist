# Riru - Clipboard Whitelist

A module of [Riru](https://github.com/RikkaApps/Riru)/[Zygisk](https://github.com/topjohnwu/zygisk-module-sample). Allowing apps to access the clipboard in the background on Android 10.

## Requirements

* Supported zygote injector enabled
  - Riru >= 26.0
  - Zygisk
* Android 10+


## Feature

Allowing apps to access the clipboard in the background on Android 10+.


## Build

1. Install JDK, Android SDK

2. Configure SDK path in local.properties 

   ```properties
   sdk.dir=/path/to/android/sdk
   ```

3. Configure built-in manager app signing in signing.properties

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
    
4. Pick `<injector>-clipboard-whitelist-<version>.zip` from `module/build/outputs/magisk/<injector>/<build-type>`
