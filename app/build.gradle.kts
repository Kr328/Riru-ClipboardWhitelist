import java.util.Properties

plugins {
    id("com.android.application")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.2")

    defaultConfig {
        applicationId = "com.github.kr328.clipboard"

        minSdkVersion(29)
        targetSdkVersion(30)

        versionCode = 6
        versionName = "v6"
    }

    buildFeatures {
        buildConfig = false
        prefab = true
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            signingConfig = signingConfigs.create("release")
        }
    }

    signingConfigs {
        named("release") {
            val properties = Properties().apply {
                load(rootProject.file("keystore.properties").inputStream())
            }

            keyAlias(properties.getProperty("keyAlias") ?: error("keystore.properties invalid"))
            keyPassword(properties.getProperty("keyPassword") ?: error("keystore.properties invalid"))
            storeFile(rootProject.file(properties.getProperty("storeFile") ?: error("keystore.properties invalid")))
            storePassword(properties.getProperty("storePassword") ?: error("keystore.properties invalid"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(project(":shared"))
}

repositories {
    mavenCentral()
}