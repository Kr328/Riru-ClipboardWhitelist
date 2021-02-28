import java.net.URI

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
    }
}

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

        val buildNdkVersion: String by extra("22.0.7026061")
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}