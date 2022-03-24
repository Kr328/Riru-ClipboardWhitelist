import com.github.kr328.gradle.zygote.ZygoteLoader.*

plugins {
    alias(deps.plugins.android.application)
    alias(deps.plugins.zygote)
    alias(deps.plugins.refine)
}

android {
    sourceSets {
        all {
            assets.srcDir(buildDir.resolve("intermediates/manager_apk/$name"))
        }
    }
}

zygote {
    val moduleId = "clipboard_whitelist"
    val moduleName = "Clipboard Whitelist"
    val moduleDescription = "Allow apps access clipboard in background."
    val moduleAuthor = "Kr328"
    val moduleEntrypoint = "com.github.kr328.clipboard.Main"

    packages(PACKAGE_SYSTEM_SERVER)

    riru {
        id = "riru_$moduleId"
        name = "Riru - $moduleName"
        author = moduleAuthor
        description = moduleDescription
        entrypoint = moduleEntrypoint
        archiveName = "${id.replace('_', '-')}-${android.defaultConfig.versionName}"
        updateJson = "https://github.com/Kr328/Riru-ClipboardWhitelist/releases/latest/download/${id.replace('_', '-')}.json"
    }

    zygisk {
        id = "zygisk_$moduleId"
        name = "Zygisk - $moduleName"
        author = moduleAuthor
        description = moduleDescription
        entrypoint = moduleEntrypoint
        archiveName = "${id.replace('_', '-')}-${android.defaultConfig.versionName}"
        updateJson = "https://github.com/Kr328/Riru-ClipboardWhitelist/releases/latest/download/${id.replace('_', '-')}.json"
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

    implementation(deps.refine.runtime)
    implementation(deps.magic.library)
}