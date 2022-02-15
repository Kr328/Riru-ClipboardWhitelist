import com.github.kr328.zloader.gradle.ZygoteLoader
import com.github.kr328.zloader.gradle.util.toCapitalized

plugins {
    id("com.android.application")
    id("zygote-loader")
    id("dev.rikka.tools.refine.gradle-plugin")
}

zygote {
    val moduleId = "clipboard_whitelist"
    val moduleName = "Clipboard Whitelist"
    val moduleDescription = "Allow apps access clipboard in background."
    val moduleAuthor = "Kr328"
    val moduleEntrypoint = "com.github.kr328.clipboard.Main"

    packages(ZygoteLoader.PACKAGE_SYSTEM_SERVER)

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
            (tasks["packageMagisk${name.toCapitalized()}"] as Zip).apply {
                dependsOn(project(":app").tasks["assemble${buildType.toCapitalized()}"])

                from(project(":app").extra["apk$buildType"]!!) {
                    into("system/app/ClipboardWhitelist")
                    include("*.apk")
                    rename {
                        "ClipboardWhitelist.apk"
                    }
                }
            }
        }
    }
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(project(":shared"))

    implementation(deps.refine.runtime)
    implementation(deps.magic.library)
}