import com.android.build.gradle.BaseExtension
import java.io.FileNotFoundException
import java.util.*

plugins {
    alias(deps.plugins.android.application) apply false
    alias(deps.plugins.android.library) apply false
}

subprojects {
    val isApp = when (name) {
        "app", "module" -> true
        else -> false
    }

    apply(plugin = if (isApp) "com.android.application" else "com.android.library")

    extensions.configure<BaseExtension> {
        compileSdkVersion(31)

        defaultConfig {
            if (isApp) {
                applicationId = "com.github.kr328.clipboard"
            }

            minSdk = 29
            targetSdk = 31

            versionName = "v22"
            versionCode = 22

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
