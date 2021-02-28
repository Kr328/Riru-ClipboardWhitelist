import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.tasks.Copy

abstract class GenerateMagiskTask : Copy() {
    fun fromApplicationVariant(variant: ApplicationVariant) {
        val extension = project.extensions.getByType(RiruExtension::class.java)

        from(project.file("src/main/raw"))

        filter {
            REGEX_PLACEHOLDER.replace(it) { result ->
                when (result.groupValues[1]) {
                    "MAGISK_ID" -> extension.id
                    "MAIGKS_NAME" -> extension.name
                    "MAGISK_VERSION_NAME" -> variant.versionName!!
                    "MAGISK_VERSION_CODE" -> variant.versionCode.toString()
                    "MAGISK_AUTHOR" -> extension.author
                    "MAGISK_DESCRIPTION" -> extension.description
                    "RIRU_NAME" -> extension.name.removePrefix("Riru - ")
                    "RIRU_VERSION_NAME" -> variant.versionName!!
                    "RIRU_VERSION_CODE" -> variant.versionCode.toString()
                    "RIRU_AUTHOR" -> extension.author
                    "RIRU_DESCRIPTION" -> extension.description
                    "RIRU_API" -> extension.minApi.toString()
                    "RIRU_MODULE_ID" -> extension.riruId
                    "RIRU_MIN_API_VERSION" -> extension.minApi.toString()
                    "RIRU_MIN_VERSION_NAME" -> extension.minApiName
                    "RURU_MIN_SDK_VERSION" -> variant.packageApplicationProvider.get()
                        .minSdkVersion.get().toString()
                    else -> ""
                }
            }
        }

        into(project.generatedMagiskDir(variant))
    }

    companion object {
        private val REGEX_PLACEHOLDER = Regex("%%%([0-9a-zA-Z_]+)%%%")
    }
}