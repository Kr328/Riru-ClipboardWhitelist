import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.tasks.Copy

abstract class ExtractApkTask : Copy() {
    fun fromApplicationVariant(variant: ApplicationVariant) {
        val apk = variant.outputs.find { it.outputFile.extension == "apk" }!!.outputFile

        from(project.zipTree(apk))

        include("classes.dex", "lib/**")

        into(project.extractedApkDir(variant))
    }
}