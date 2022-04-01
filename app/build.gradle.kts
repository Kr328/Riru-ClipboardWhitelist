import com.android.build.api.artifact.SingleArtifact

plugins {
    id("com.android.application")
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(project(":shared"))
}