import java.util.Properties

plugins {
    id("com.android.application")
}

val buildMinVersion: Int by extra
val buildTargetVersion: Int by extra

val buildVersionCode: Int by extra
val buildVersionName: String by extra

android {
    compileSdkVersion(buildTargetVersion)

    defaultConfig {
        applicationId = "com.github.kr328.clipboard"

        minSdk = buildMinVersion
        targetSdk = buildTargetVersion

        versionCode = buildVersionCode
        versionName = buildVersionName
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
                rootProject.file("keystore.properties").inputStream().use(this::load)
            }

            storeFile(rootProject.file(properties.getProperty("storeFile") ?: error("keystore.properties invalid")))
            storePassword(properties.getProperty("storePassword") ?: error("keystore.properties invalid"))
            keyAlias(properties.getProperty("keyAlias") ?: error("keystore.properties invalid"))
            keyPassword(properties.getProperty("keyPassword") ?: error("keystore.properties invalid"))
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