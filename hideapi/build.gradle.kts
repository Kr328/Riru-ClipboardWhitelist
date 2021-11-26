plugins {
    id("com.android.library")
}

dependencies {
    annotationProcessor(deps.refine.processor)

    compileOnly(deps.refine.annotation)
}