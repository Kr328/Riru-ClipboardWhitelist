plugins {
    id("com.android.application")
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(project(":shared"))
}