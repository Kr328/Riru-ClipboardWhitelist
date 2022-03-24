import com.android.build.gradle.BaseExtension
import java.io.FileNotFoundException
import java.util.*

plugins {
    val agp = "7.1.2"
    val zygote = "2.6"
    val refine = "3.1.0"

    id("com.android.application") version agp apply false
    id("com.android.library") version agp apply false
    id("com.github.kr328.gradle.zygote") version zygote apply false
    id("dev.rikka.tools.refine") version refine apply false
}

subprojects {
    val configureBaseExtension: BaseExtension.(isApp: Boolean) -> Unit = { isApp: Boolean ->
        compileSdkVersion(31)

        defaultConfig {
            if (isApp) {
                applicationId = "com.github.kr328.clipboard"
            }

            minSdk = 29
            targetSdk = 31

            versionName = "v23"
            versionCode = 23

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

    plugins.withId("com.android.application") {
        extensions.configure<BaseExtension> {
            configureBaseExtension(true)
        }
    }
    plugins.withId("com.android.library") {
        extensions.configure<BaseExtension> {
            configureBaseExtension(false)
        }
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}
