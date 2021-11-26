import com.github.kr328.zloader.gradle.ZygoteLoader
import com.github.kr328.zloader.gradle.tasks.PackageMagiskTask
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
    }
}

androidComponents {
    onVariants {
        val name = it.name
        val flavorName = it.flavorName!!
        val buildType = it.buildType!!

        afterEvaluate {
            (tasks[PackageMagiskTask.taskName(name)] as Zip).apply {
                archiveBaseName.set("$flavorName-clipboard-whitelist-${android.defaultConfig.versionName}")

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
}