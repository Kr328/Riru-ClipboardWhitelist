plugins {
    id("com.android.library")
}

val buildMinVersion: Int by extra
val buildTargetVersion: Int by extra

val buildVersionCode: Int by extra
val buildVersionName: String by extra

android {
    compileSdkVersion(buildTargetVersion)

    defaultConfig {
        minSdk = buildMinVersion
        targetSdk = buildTargetVersion

        versionCode = buildVersionCode
        versionName = buildVersionName
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

}

repositories {
    mavenCentral()
}