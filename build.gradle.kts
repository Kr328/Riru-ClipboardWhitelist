@file:Suppress("UNUSED_VARIABLE")

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    extra {
        val buildMinVersion: Int by extra(29)
        val buildTargetVersion: Int by extra(30)

        val buildVersionCode: Int by extra(10)
        val buildVersionName: String by extra("v10")

        val buildNdkVersion: String by extra("23.0.7123448")
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}
