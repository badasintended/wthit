import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class FormatTranslationTask : DefaultTask() {

    @get:InputDirectory
    abstract val translationDir: Property<File>

    @get:Input
    abstract val test: Property<Boolean>

    init {
        test.convention(false)
    }

    @TaskAction
    fun format() {
        val lines = translationDir.get()
            .resolve("en_us.json")
            .readLines()
        val lineNumber = lines
            .mapIndexedNotNull { i, s -> Regex("""\s*"(.*?)".*""").matchEntire(s)?.groups?.get(1)?.value?.let { it to i } }
            .toMap()

        val maxKeyLength = lineNumber.keys.stream().mapToInt { it.length }.max().asInt + 2

        translationDir.get().listFiles { _, name -> name != "en_us.json" }?.forEach { file ->
            println("Formatting ${file.nameWithoutExtension}")

            val language = JsonParser.parseString(file.readText()) as JsonObject
            val output = lines.toMutableList()

            for (key in language.keySet()) {
                if (!lineNumber.containsKey(key)) {
                    logger.error("\tUnknown key ${key}")
                    continue
                }

                val value = language.get(key).toString()
                output[lineNumber[key]!!] = "  " + "\"${key}\"".padEnd(maxKeyLength) + ": ${value},"
            }

            lineNumber.forEach { key, line ->
                if (!language.has(key)) {
                    logger.warn("\tMissing translaton ${key}")
                    output[line] = ""
                }
            }

            val lastTranslated = output.indexOfLast { it.endsWith(",") }
            output[lastTranslated] = output[lastTranslated].removeSuffix(",")

            val outputFile = if (test.get()) project.buildDir.resolve("formatTranslation/${file.name}") else file
            outputFile.parentFile.mkdirs()
            outputFile.writeText(output.joinToString(separator = "\n", postfix = "\n"))
        }
    }

}
