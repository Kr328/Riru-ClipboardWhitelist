import com.android.build.gradle.AppExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class HideapiRedefinePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (!target.pluginManager.hasPlugin("com.android.base"))
            throw GradleException("`com.android.base` not found")

        decorateAndroidExtension(target)
    }

    private fun decorateAndroidExtension(target: Project) {
        target.extensions.getByType(AppExtension::class.java).apply {
            registerTransform(HideapiRedefineTransform())
        }
    }
}