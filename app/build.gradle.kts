plugins {
    id("com.android.application")
}

android {
    namespace = "com.github.kr328.clipboard"
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(project(":shared"))
}
