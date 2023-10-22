import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import java.util.*

plugins {
    val agp = "8.1.2"
    val zygote = "3.1"
    val refine = "4.3.1"

    id("com.android.application") version agp apply false
    id("com.android.library") version agp apply false
    id("com.github.kr328.gradle.zygote") version zygote apply false
    id("dev.rikka.tools.refine") version refine apply false
}

subprojects {
    plugins.withId("com.android.base") {
        extensions.configure<BaseExtension> {
            val isApp = this is AppExtension

            compileSdkVersion(34)

            defaultConfig {
                if (isApp) {
                    applicationId = "com.github.kr328.clipboard"
                }

                minSdk = 29
                targetSdk = 34

                versionName = "v26"
                versionCode = 26

                if (!isApp) {
                    consumerProguardFiles("consumer-rules.pro")
                }
            }

            if (isApp) {
                signingConfigs {
                    create("release") {
                        val prop = Properties().apply {
                            rootProject.file("signing.properties").reader().use(this::load)
                        }

                        storeFile = rootProject.file(prop.getProperty("keystore.path"))
                        storePassword = prop.getProperty("keystore.password")
                        keyAlias = prop.getProperty("key.alias")
                        keyPassword = prop.getProperty("key.password")
                    }
                }
            }

            buildTypes {
                named("release") {
                    isMinifyEnabled = isApp
                    isShrinkResources = isApp
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                    if (isApp) {
                        signingConfig = signingConfigs.getByName("release")
                    }
                }
            }
        }
    }
}

task("clean", type = Delete::class) {
    delete(layout.buildDirectory)
}
