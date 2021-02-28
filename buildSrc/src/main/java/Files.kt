import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Project
import java.io.File

val Project.intermediatesDir: File
    get() = buildDir.resolve("intermediates")

fun Project.extractedApkDir(v: ApplicationVariant): File {
    return intermediatesDir.resolve("apk_files/${v.name}")
}

fun Project.generatedMagiskDir(v: ApplicationVariant): File {
    return intermediatesDir.resolve("magisk_files/${v.name}")
}

fun Project.generatedMagiskChecksumDir(v: ApplicationVariant): File {
    return intermediatesDir.resolve("magisk_files_checksum/${v.name}")
}
