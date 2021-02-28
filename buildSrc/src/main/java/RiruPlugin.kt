import com.android.build.gradle.AppExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

class RiruPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (!target.plugins.hasPlugin("com.android.application")) {
            throw GradleException("module must apply 'com.android.application'")
        }

        target.extensions.create("riru", RiruExtension::class.java)

        target.afterEvaluate {
            decorateAndroidExtension(target)
        }
    }

    private fun decorateAndroidExtension(target: Project) {
        target.extensions.getByType(AppExtension::class.java).applicationVariants.forEach { v ->
            val cName = v.name.capitalize(Locale.getDefault())

            val extract = target.tasks.register("extractApk${cName}", ExtractApkTask::class.java) {
                it.fromApplicationVariant(v)
            }

            val generate =
                target.tasks.register("generateMagisk${cName}", GenerateMagiskTask::class.java) {
                    it.fromApplicationVariant(v)
                }

            val merge = target.tasks.register("mergeMagisk${cName}", MergeMagiskTask::class.java) {
                it.fromApplicationVariant(v)
            }

            val checksum = target.tasks.register(
                "generateChecksum${cName}",
                GenerateChecksumTask::class.java
            ) {
                it.fromApplicationVariant(v)
            }

            val packages =
                target.tasks.register("packageMagisk${cName}", PackageMagiskTask::class.java) {
                    it.fromApplicationVariant(v)
                }

            extract.get().dependsOn(v.packageApplicationProvider.get())
            merge.get().dependsOn(extract.get())
            checksum.get().dependsOn(generate.get(), merge.get())
            packages.get().dependsOn(generate.get(), merge.get(), checksum.get())

            v.assembleProvider.get().dependsOn(packages.get())
        }
    }
}