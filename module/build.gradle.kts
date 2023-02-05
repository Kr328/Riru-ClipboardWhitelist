import com.github.kr328.gradle.zygote.ZygoteLoader.*

plugins {
    id("com.android.application")
    id("com.github.kr328.gradle.zygote")
    id("dev.rikka.tools.refine")
}

android {
    namespace = "com.github.kr328.module"

    sourceSets {
        all {
            assets.srcDir(buildDir.resolve("intermediates/manager_apk/$name"))
        }
    }
}

zygote {
    val moduleId = "clipboard-whitelist"
    val moduleName = "Clipboard Whitelist"
    val moduleDescription = "Allow apps access clipboard in background."
    val moduleAuthor = "Kr328"
    val moduleEntrypoint = "com.github.kr328.clipboard.Main"
    val versionName = android.defaultConfig.versionName

    packages(PACKAGE_SYSTEM_SERVER)

    riru {
        id = "riru-$moduleId".replace('-', '_')
        name = "Riru - $moduleName"
        archiveName = "riru-$moduleId-$versionName"
        updateJson = "https://github.com/Kr328/Riru-ClipboardWhitelist/releases/latest/download/riru-$moduleId.json"
    }

    zygisk {
        id = "zygisk-$moduleId".replace('-', '_')
        name = "Zygisk - $moduleName"
        archiveName = "zygisk-$moduleId-$versionName"
        updateJson = "https://github.com/Kr328/Riru-ClipboardWhitelist/releases/latest/download/zygisk-$moduleId.json"
    }

    all {
        author = moduleAuthor
        description = moduleDescription
        entrypoint = moduleEntrypoint
        isUseBinderInterceptors = true
    }
}

androidComponents {
    onVariants {
        val name = it.name
        val buildType = it.buildType!!

        afterEvaluate {
            val packaging = project(":app").tasks["package${buildType.capitalize()}"]
            val syncManager = task("syncManagerApk${name.capitalize()}", Sync::class) {
                dependsOn(packaging)

                destinationDir = buildDir.resolve("intermediates/manager_apk/$name")

                from(packaging.outputs) {
                    include("*.apk")
                    into("system/app/ClipboardWhitelist")
                    rename { "ClipboardWhitelist.apk" }
                }
            }
            tasks["merge${name.capitalize()}Assets"].dependsOn(syncManager)
            tasks.findByName("lintVitalAnalyze${name.capitalize()}")?.dependsOn(syncManager)
        }
    }
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(project(":shared"))

    implementation(libs.refine.runtime)
    implementation(libs.magic.library)
}

evaluationDependsOn(":app")
