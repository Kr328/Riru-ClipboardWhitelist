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
    val moduleEntrypoint = "com.github.kr328.clipboard.Injector"

    packages(ZygoteLoader.PACKAGE_SYSTEM_SERVER)

    riru {
        id = "riru_$moduleId"
        name = "Riru - $moduleName"
        author = moduleAuthor
        description = moduleDescription
        entrypoint = moduleEntrypoint
        archiveName = "riru-${moduleId.replace('_', '-')}-${android.defaultConfig.versionName}"
    }

    zygisk {
        id = "zygisk_$moduleId"
        name = "Zygisk - $moduleName"
        author = moduleAuthor
        description = moduleDescription
        entrypoint = moduleEntrypoint
        archiveName = "zygisk-${moduleId.replace('_', '-')}-${android.defaultConfig.versionName}"
    }
}

androidComponents {
    onVariants {
        val name = it.name
        val buildType = it.buildType!!

        afterEvaluate {
            (tasks["packageMagisk${name.toCapitalized()}"] as Zip).apply {
                val appApk = project(":app").buildDir
                    .resolve("outputs/apk/${buildType}/app-${buildType}.apk")

                from(appApk) {
                    into("system/app/ClipboardWhitelist")
                    rename {
                        "ClipboardWhitelist.apk"
                    }
                }

                dependsOn(project(":app").tasks["assemble${buildType.toCapitalized()}"])
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