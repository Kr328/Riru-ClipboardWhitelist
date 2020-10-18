# Riru - IFWEnhance

A module of [Riru](https://github.com/RikkaApps/Riru). Enhance Intent Firewall.

## Requirements

* [Riru](https://github.com/RikkaApps/Riru) > 19 installed.
* Android 9.0 (preview not tested)



## Feature

Enhance Intent Firewall

* Apply Intent Firewall for **Implicit intents** (`PackageManager.queryIntentActivities`)



## Build

1. Install JDK ,Gradle ,Android SDK ,Android NDK

2. Configure local.properties 

   ```properties
   ndk.dir=/path/to/android/ndk
   sdk.dir=/path/to/android/sdk
   ```

3. Run command 

    ``` bash 
    ./gradlew app:assembleRelease
    ```
    
4. Pick riru-ifw-enhance.zip from module/build/outputs



## Feedback

Telegram Group [Kr328 Riru Modules](https://t.me/kr328_riru_modules)
