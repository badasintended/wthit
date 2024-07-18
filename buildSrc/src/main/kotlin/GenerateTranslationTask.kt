import com.google.common.base.CaseFormat
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.enterprise.test.FileProperty
import java.io.File
import javax.lang.model.element.Modifier

abstract class GenerateTranslationTask : DefaultTask() {
    @get:InputFile
    abstract val input: RegularFileProperty

    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:Input
    abstract val className: Property<String>

    @get:Input
    abstract val skipPaths: SetProperty<String>

    init {
        skipPaths.convention(setOf())
    }

    @TaskAction
    fun generate() {
        val json = JsonParser.parseString(input.get().asFile.readText()) as JsonObject

        val types = hashMapOf<String, TypeSpec.Builder>()

        types[""] = TypeSpec.classBuilder(this.className.get().substringAfterLast('.'))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addJavadoc("Generated constant class, do NOT modify!")

        for (key in json.keySet()) {
            if (key.endsWith("_desc")) continue
            if (key.startsWith("config") && key.contains(Regex("plugin_.+"))) continue

            val paths = listOf("") + key.split('.').filterNot { skipPaths.get().contains(it) }

            var parent = ""
            paths.subList(0, paths.lastIndex).forEachIndexed { i, path ->
                if (i > 1) parent += "."
                parent += path

                types.computeIfAbsent(parent) {
                    TypeSpec.classBuilder(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, path))
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                }
            }

            types[parent]!!.addField(
                FieldSpec.builder(String::class.java, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, paths.last()))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("\$S", key)
                    .build()
            )
        }


        types.keys.stream()
            .filter { it != "" }
            .sorted(Comparator.comparingInt<String> { path -> path.count { it == '.' } }.reversed())
            .forEach { path ->
                var parent = path.substringBeforeLast('.')
                if (parent == path) parent = ""

                types[parent]!!.addType(types[path]!!.build())
            }

        JavaFile.builder(this.className.get().substringBeforeLast('.'), types[""]!!.build())
            .build()
            .writeTo(output.get().asFile.toPath(), Charsets.UTF_8)
    }


}
