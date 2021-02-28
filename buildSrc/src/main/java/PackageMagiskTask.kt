import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.tasks.bundling.Zip

abstract class PackageMagiskTask : Zip() {
    fun fromApplicationVariant(variant: ApplicationVariant) {
        val extension = project.extensions.getByType(RiruExtension::class.java)

        from(project.generatedMagiskDir(variant))
        from(project.generatedMagiskChecksumDir(variant))

        exclude("lib")

        eachFile {
            if (it.path.startsWith("dist-"))
                it.path = "." + it.path.removePrefix("dist-")
        }

        destinationDirectory.set(project.buildDir.resolve("outputs"))
        archiveFileName.set(extension.id.replace('_', '-') + "-${variant.name}" + ".zip")
    }
}