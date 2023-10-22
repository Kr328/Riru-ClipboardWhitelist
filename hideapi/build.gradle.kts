plugins {
    id("com.android.library")
}

android {
    namespace = "com.github.kr328.hideapi"
}

dependencies {
    annotationProcessor(libs.refine.processor)

    compileOnly(libs.refine.annotation)
}
