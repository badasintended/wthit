import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.getByType

fun Jar.fromCommonOutput() {
    project.commonProject.extensions.getByType<SourceSetContainer>()
        .filterNot { it.name == "pluginTest" }
        .forEach {
            from(it.output) {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
        }
}

fun Jar.fromCommonSources() {
    project.commonProject.extensions.getByType<SourceSetContainer>()
        .filterNot { it.name == "pluginTest" }
        .forEach {
            from(it.allSource) {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
        }
}
