import org.gradle.kotlin.dsl.support.unzipTo
import org.gradle.kotlin.dsl.support.zipTo
import java.security.MessageDigest

plugins {
    id("com.android.application")
}

val riruId = "clipboard_whitelist"
val riruApi = 9
val riruName = "v22.0"

val moduleId = "riru_clipboard_whitelist"
val moduleName = "Riru - Clipboard Whitelist"
val moduleDescription = "A module of Riru. Add clipboard whitelist to Android 10."
val moduleAuthor = "Kr328"
val moduleFiles = listOf(
        "system/framework/$riruId.dex",
        "system/app/ClipboardWhitelist/ClipboardWhitelist.apk"
)

val binaryTypes = setOf("dex", "so", "apk")

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.2")

    ndkVersion = "21.3.6528147"

    defaultConfig {
        applicationId = "com.github.kr328.clipboard.module"

        minSdkVersion(29)
        targetSdkVersion(30)

        versionCode = 5
        versionName = "v5"

        multiDexEnabled = false

        externalNativeBuild {
            cmake {
                arguments(
                        "-DRIRU_API:INTEGER=$riruApi",
                        "-DRIRU_NAME:STRING=$riruName",
                        "-DRIRU_MODULE_ID:STRING=$riruId",
                        "-DRIRU_MODULE_VERSION_CODE:INTEGER=$versionCode",
                        "-DRIRU_MODULE_VERSION_NAME:STRING=$versionName"
                )
            }
        }
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

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    applicationVariants.all {
        val task = assembleProvider?.get() ?: error("assemble task not found")
        val prefix = moduleId.replace('_', '-')
        val zipFile = buildDir.resolve("outputs/$prefix-$name.zip")
        val zipContent = buildDir.resolve("intermediates/magisk/$name")
        val apkFile = this.outputs.first()?.outputFile ?: error("apk not found")
        val minSdkVersion = packageApplicationProvider?.get()?.minSdkVersion?.get() ?: error("invalid min sdk version")
        val regexPlaceholder = Regex("%%%(\\S+)%%%")
        val variant = this.name

        task.doLast {
            zipContent.deleteRecursively()

            zipContent.mkdirs()

            val apkTree = zipTree(apkFile)

            copy {
                into(zipContent)

                from(file("src/main/raw")) {
                    exclude("riru.sh", "module.prop", "riru/module.prop.new", "dist-gitattributes")
                }

                from(file("src/main/raw/dist-gitattributes")) {
                    rename { ".gitattributes" }
                }

                from(file("src/main/raw/riru.sh")) {
                    filter { line ->
                        line.replace(regexPlaceholder) {
                            when (it.groupValues[1]) {
                                "RIRU_MODULE_ID" -> riruId
                                "RIRU_MIN_API_VERSION" -> riruApi.toString()
                                "RIRU_MIN_VERSION_NAME" -> riruName
                                "RURU_MIN_SDK_VERSION" -> minSdkVersion.toString()
                                else -> ""
                            }
                        }
                    }
                }

                from(file("src/main/raw/module.prop")) {
                    filter { line ->
                        line.replace(regexPlaceholder) {
                            when (it.groupValues[1]) {
                                "MAGISK_ID" -> moduleId
                                "MAIGKS_NAME" -> moduleName
                                "MAGISK_VERSION_NAME" -> versionName!!
                                "MAGISK_VERSION_CODE" -> versionCode.toString()
                                "MAGISK_AUTHOR" -> moduleAuthor
                                "MAGISK_DESCRIPTION" -> moduleDescription
                                else -> ""
                            }
                        }
                    }
                }

                from(file("src/main/raw/riru/module.prop.new")) {
                    into("riru/")

                    filter { line ->
                        line.replace(regexPlaceholder) {
                            when (it.groupValues[1]) {
                                "RIRU_NAME" -> moduleName.removePrefix("Riru - ")
                                "RIRU_VERSION_NAME" -> versionName!!
                                "RIRU_VERSION_CODE" -> versionCode.toString()
                                "RIRU_AUTHOR" -> moduleAuthor
                                "RIRU_DESCRIPTION" -> moduleDescription
                                "RIRU_API" -> riruApi.toString()
                                else -> ""
                            }
                        }
                    }
                }

                from(apkTree) {
                    include("lib/**")
                    eachFile {
                        path = path
                                .replace("lib/x86_64", "system_x86/lib64")
                                .replace("lib/x86", "system_x86/lib")
                                .replace("lib/arm64-v8a", "system/lib64")
                                .replace("lib/armeabi-v7a", "system/lib")
                    }
                }

                from(apkTree) {
                    include("classes.dex")
                    eachFile {
                        path = "system/framework/$riruId.dex"
                    }
                }

                from(project(":app").buildDir.resolve("outputs/apk/$variant/app-$variant.apk")) {
                    into("system/app/ClipboardWhitelist")
                    rename { "ClipboardWhitelist.apk" }
                }
            }

            zipContent.resolve("extras.files")
                    .writeText(moduleFiles.joinToString("\n") + "\n")

            fileTree(zipContent)
                    .filter { it.isFile }
                    .filterNot { it.extension in binaryTypes }
                    .forEach { it.writeText(it.readText().replace("\r\n", "\n")) }

            fileTree(zipContent)
                    .matching { exclude("customize.sh", "verify.sh", "META-INF", "README.md") }
                    .filter { it.isFile }
                    .forEach {
                        val sha256sum = MessageDigest.getInstance("SHA-256").digest(it.readBytes())
                        val sha256text = sha256sum.joinToString(separator = "") { b ->
                            String.format("%02x", b.toInt() and 0xFF)
                        }

                        File(it.absolutePath + ".sha256sum").writeText(sha256text)
                    }

            zipTo(zipFile, zipContent)
        }
    }
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(project(":shared"))

    implementation("rikka.ndk:riru:9.1")
}