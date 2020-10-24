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

        versionCode = 5
        versionName = "v5"
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
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(project(":shared"))
}

repositories {
    mavenCentral()
}