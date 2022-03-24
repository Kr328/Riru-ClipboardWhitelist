plugins {
    alias(deps.plugins.android.library)
}

dependencies {
    annotationProcessor(deps.refine.processor)

    compileOnly(deps.refine.annotation)
}