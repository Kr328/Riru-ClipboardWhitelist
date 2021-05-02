plugins {
    id("com.android.application")
    id("hideapi-redefine")
    id("riru")
}

val buildMinVersion: Int by extra
val buildTargetVersion: Int by extra

val buildVersionCode: Int by extra
val buildVersionName: String by extra

val buildNdkVersion: String by extra

riru {
    id = "riru_clipboard_whitelist"
    name = "Riru - Clipboard Whitelist"
    minApi = 28
    minApiName = "25.0.0"
    description = "A module of Riru. Add clipboard whitelist to Android 10."
    author = "Kr328"
}

android {
    compileSdkVersion(buildTargetVersion)

    ndkVersion = buildNdkVersion

    defaultConfig {
        applicationId = "com.github.kr328.clipboard.module"

        minSdk = buildMinVersion
        targetSdk = buildTargetVersion

        versionCode = buildVersionCode
        versionName = buildVersionName

        multiDexEnabled = false

        externalNativeBuild {
            cmake {
                arguments(
                        "-DRIRU_API:INTEGER=${riru.minApi}",
                        "-DRIRU_NAME:STRING=${riru.name}",
                        "-DRIRU_MODULE_ID:STRING=${riru.riruId}",
                        "-DRIRU_MODULE_VERSION_CODE:INTEGER=$versionCode",
                        "-DRIRU_MODULE_VERSION_NAME:STRING=$versionName"
                )
            }
        }
    }

    buildFeatures {
        buildConfig = false
        prefab = true
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    applicationVariants.all {

    }
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(project(":shared"))

    implementation("dev.rikka.ndk:riru:25.0.1")
}

afterEvaluate {
    android.applicationVariants.forEach {
        val cName = it.name.capitalize()

        val cp = tasks.register("copyModuleApk$cName", Copy::class.java) {
            from(project(":app").buildDir
                .resolve("outputs/apk/${it.name}/app-${it.name}.apk"))

            into(generatedMagiskDir(it)
                .resolve("system/priv-app/ClipboardWhitelist"))

            rename {
                "ClipboardWhitelist.apk"
            }
        }

        tasks["mergeMagisk$cName"].dependsOn(cp)
        cp.get().dependsOn(project(":app").tasks["assemble$cName"])
    }
}