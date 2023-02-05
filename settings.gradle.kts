rootProject.name = "Clipboard Whitelist"

include(":shared", ":hideapi", ":module", ":app")

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven(url = "https://maven.kr328.app/releases")
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://maven.kr328.app/releases")
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            val refine = "4.0.0"
            val magic = "1.5"

            library("refine-processor", "dev.rikka.tools.refine:annotation-processor:$refine")
            library("refine-annotation", "dev.rikka.tools.refine:annotation:$refine")
            library("refine-runtime", "dev.rikka.tools.refine:runtime:$refine")
            library("magic-library", "com.github.kr328.magic:library:$magic")
        }
    }
}
