import java.net.URI

buildscript {
    var kotlin_version: String by extra

    kotlin_version = "1.4.10"

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = URI("https://dl.bintray.com/rikkaw/Libraries") }
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}