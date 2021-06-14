import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.modOptional(any: String, config: ExternalModuleDependency.() -> Unit = {}) {
    "modCompileOnly"(any, config)
    "modRuntime"(any, config)
}