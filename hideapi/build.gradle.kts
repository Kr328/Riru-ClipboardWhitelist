plugins {
    id("com.android.library")
}

dependencies {
    annotationProcessor(libs.refine.processor)

    compileOnly(libs.refine.annotation)
}