import com.android.build.api.transform.*
import javassist.bytecode.ClassFile
import java.io.*
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class HideapiRedefineTransform : Transform() {
    override fun getName(): String = "HideapiRedefine"

    override fun isIncremental(): Boolean = true

    override fun getInputTypes(): Set<QualifiedContent.ContentType> =
        setOf(QualifiedContent.DefaultContentType.CLASSES)

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        mutableSetOf(
            QualifiedContent.Scope.PROJECT,
            QualifiedContent.Scope.SUB_PROJECTS,
            QualifiedContent.Scope.EXTERNAL_LIBRARIES,
        )

    override fun transform(transformInvocation: TransformInvocation) {
        println("isIncremental = " + transformInvocation.isIncremental)

        if (!transformInvocation.isIncremental)
            transformInvocation.outputProvider.deleteAll()

        for (inputJar in collectChangedJars(transformInvocation)) {
            val inputFile = inputJar.file
            val outputFile = transformInvocation.outputProvider
                .getContentLocation(
                    inputJar.name,
                    inputJar.contentTypes,
                    inputJar.scopes,
                    Format.JAR
                )

            if (inputFile == null || !inputFile.exists()) {
                outputFile.delete()

                continue
            }

            val inputStream: ZipInputStream = JarInputStream(FileInputStream(inputFile))
            val outputStream: ZipOutputStream = JarOutputStream(FileOutputStream(outputFile))

            while (true) {
                val entry = inputStream.nextEntry ?: break

                if (entry.name.endsWith(".class")) {
                    outputStream.putNextEntry(ZipEntry(replaceName(entry.name)))

                    val bufferedIn = BufferedInputStream(inputStream)
                    val bufferedOut = BufferedOutputStream(outputStream)

                    patchClass(bufferedIn, bufferedOut)

                    bufferedOut.flush()
                } else {
                    outputStream.putNextEntry(ZipEntry(entry.name))

                    inputStream.copyTo(outputStream)
                }

                inputStream.closeEntry()
                outputStream.closeEntry()
            }

            inputStream.close()
            outputStream.close()
        }

        for (directory in transformInvocation.inputs.flatMap { it.directoryInputs }) {
            for ((inputFile, relativePath) in collectChangedFiles(
                directory,
                transformInvocation.isIncremental
            )) {
                if (inputFile.extension != "class") {
                    val outputFile = transformInvocation.outputProvider.getContentLocation(
                        directory.name,
                        directory.contentTypes,
                        directory.scopes,
                        Format.DIRECTORY
                    ).resolve(relativePath)

                    if (!inputFile.exists()) {
                        outputFile.delete()

                        continue
                    }

                    outputFile.parentFile?.mkdirs()

                    inputFile.copyTo(outputFile)

                    continue
                }

                val outputFile = transformInvocation.outputProvider.getContentLocation(
                    directory.name, directory.contentTypes, directory.scopes, Format.DIRECTORY
                ).resolve(replaceName(relativePath))

                if (!inputFile.exists()) {
                    outputFile.delete()

                    continue
                }

                outputFile.parentFile?.mkdirs()

                BufferedInputStream(FileInputStream(inputFile)).use { i ->
                    BufferedOutputStream(FileOutputStream(outputFile)).use { o ->
                        patchClass(i, o)
                    }
                }
            }
        }
    }

    private fun collectChangedJars(transform: TransformInvocation): List<JarInput> {
        return if (!transform.isIncremental) {
            transform.inputs.flatMap { t -> t.jarInputs }
        } else {
            transform.inputs
                .flatMap { s -> s.jarInputs }
                .filter { s -> s.status != Status.NOTCHANGED }
        }
    }

    private fun collectChangedFiles(
        directory: DirectoryInput,
        isIncremental: Boolean
    ): Map<File, String> {
        return if (!isIncremental) {
            directory.file.walk()
                .filter(File::isFile)
                .map { s -> s to s.relativeTo(directory.file).path }
                .toMap()
        } else {
            directory.changedFiles.asSequence()
                .filter { s -> s.key.isFile || s.value == Status.REMOVED }
                .filter { s -> s.value != Status.NOTCHANGED }
                .map { s -> s.key to s.key.relativeTo(directory.file).path }
                .toMap()
        }
    }

    private fun patchClass(input: BufferedInputStream, output: BufferedOutputStream) {
        val classFile = ClassFile(DataInputStream(input))
        val classes = classFile.constPool.classNames.filterIsInstance<String>()
        val classesReplace = classes.map { it to replaceName(it) }.toMap()

        classFile.renameClass(classesReplace)

        classFile.write(DataOutputStream(output))
    }

    private fun replaceName(original: String): String {
        return if (original.startsWith("$")) original.substring(1) else original
    }
}