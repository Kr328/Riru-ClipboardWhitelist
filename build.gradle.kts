@file:Suppress("UNUSED_VARIABLE")

import java.net.URI

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = URI("https://dl.bintray.com/rikkaw/Libraries") }
    }
    extra {
        val buildMinVersion: Int by extra(29)
        val buildTargetVersion: Int by extra(30)

        val buildVersionCode: Int by extra(7)
        val buildVersionName: String by extra("v7")

        val buildNdkVersion: String by extra("23.0.7123448")
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}
