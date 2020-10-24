import java.net.URI

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.0")
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