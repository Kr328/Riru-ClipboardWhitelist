import com.android.build.api.artifact.SingleArtifact

plugins {
    alias(deps.plugins.android.application)
}

androidComponents {
    onVariants {
        extra["apk${it.name}"] = it.artifacts.get(SingleArtifact.APK)
    }
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(project(":shared"))
}