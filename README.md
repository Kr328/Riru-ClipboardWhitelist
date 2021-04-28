# Riru - Clipboard Whitelist

A module of [Riru](https://github.com/RikkaApps/Riru). Allowing apps to access the clipboard in the background on Android 10.

## Requirements

* [Riru](https://github.com/RikkaApps/Riru) >= 25.0 installed.
* Android 10+


## Feature

Allowing apps to access the clipboard in the background on Android 10.   


## Build

1. Install JDK ,Android SDK ,Android NDK

2. Configure local.properties 

   ```properties
   ndk.dir=/path/to/android/ndk
   sdk.dir=/path/to/android/sdk
   ```

3. Configure keystore.properties

   ```properties
   keyAlias=<your key alias>
   keyPassword=<your key password>
   storeFile=/path/to/your/store/file
   storePassword=<your store password>
   ```

4. Run build command 

    ``` bash 
    ./gradlew module:assembleRelease
    ```
    
4. Pick `riru-clipboard-whitelist-release.zip` from `module/build/outputs`
