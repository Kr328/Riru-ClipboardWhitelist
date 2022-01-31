@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "Clipboard Whitelist"

include(":shared", ":hideapi", ":module", ":app")

dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            val agp = "7.1.0"
            val zloader = "1.8"
            val refine = "3.0.3"
            val magic = "1.4"

            alias("build-android").to("com.android.tools.build:gradle:$agp")
            alias("build-zloader").to("com.github.kr328.zloader:gradle-plugin:$zloader")
            alias("build-refine").to("dev.rikka.tools.refine:gradle-plugin:$refine")
            alias("refine-processor").to("dev.rikka.tools.refine:annotation-processor:$refine")
            alias("refine-annotation").to("dev.rikka.tools.refine:annotation:$refine")
            alias("refine-runtime").to("dev.rikka.tools.refine:runtime:$refine")
            alias("magic-library").to("com.github.kr328.magic:library:$magic")
        }
    }
}