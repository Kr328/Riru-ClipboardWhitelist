import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.security.MessageDigest

abstract class GenerateChecksumTask : DefaultTask() {
    abstract val input: DirectoryProperty
        @InputDirectory @Incremental @PathSensitive(PathSensitivity.RELATIVE) get

    abstract val output: DirectoryProperty
        @OutputDirectory get

    fun fromApplicationVariant(variant: ApplicationVariant) {
        input.set(project.generatedMagiskDir(variant))
        output.set(project.generatedMagiskChecksumDir(variant))
    }

    @TaskAction
    fun generate(inputChanges: InputChanges) {
        inputChanges.getFileChanges(input).forEach {
            if (it.file.isDirectory)
                return@forEach

            if (EXCLUDES.any { e -> it.normalizedPath.startsWith(e) })
                return@forEach

            val text = MessageDigest.getInstance("SHA-256").digest(it.file.readBytes())
                .joinToString("") { b ->
                    String.format("%02x", b.toInt() and 0xFF)
                }

            output.file(it.normalizedPath + ".sha256sum").get().asFile
                .apply { parentFile.mkdirs() }
                .writeText(text)
        }
    }

    companion object {
        private val EXCLUDES = setOf("customize.sh", "verify.sh", "META-INF", "README.md")
    }
}