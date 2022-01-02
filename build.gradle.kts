@file:Suppress("UNUSED_VARIABLE")

import com.android.build.gradle.BaseExtension
import java.io.FileNotFoundException
import java.util.*

buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.kr328.app/releases")
    }
    dependencies {
        classpath(deps.build.android)
        classpath(deps.build.refine)
        classpath(deps.build.zloader)
    }
}

subprojects {
    val isApp = when (name) {
        "app", "module" -> true
        else -> false
    }

    apply(plugin = if (isApp) "com.android.application" else "com.android.library")

    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.kr328.app/releases")
    }

    extensions.configure<BaseExtension> {
        val minSdkVersion = 29
        val targetSdkVersion = 31
        val buildVersionName = "v13"
        val buildVersionCode = 13

        compileSdkVersion(targetSdkVersion)

        defaultConfig {
            if (isApp) {
                applicationId = "com.github.kr328.clipboard"
            }

            minSdk = minSdkVersion
            targetSdk = targetSdkVersion

            versionName = buildVersionName
            versionCode = buildVersionCode

            if (!isApp) {
                consumerProguardFiles("consumer-rules.pro")
            }
        }

        signingConfigs {
            val file = rootProject.file("signing.properties")
            if (!file.exists()) {
                throw GradleScriptException(
                    "signing.properties required",
                    FileNotFoundException("signing.properties not found")
                )
            }

            create("release") {
                val prop = Properties().apply {
                    file.reader().use(this::load)
                }

                storeFile = rootProject.file(prop.getProperty("keystore.path"))
                storePassword = prop.getProperty("keystore.password")
                keyAlias = prop.getProperty("key.alias")
                keyPassword = prop.getProperty("key.password")
            }
        }

        buildTypes {
            named("release") {
                isMinifyEnabled = isApp
                isShrinkResources = isApp
                signingConfig = signingConfigs.getByName("release")
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}
