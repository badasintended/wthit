import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.modCompileRuntime(any: String, config: ExternalModuleDependency.() -> Unit = {}) {
    "modCompileOnly"(any, config)
    "modRuntimeOnly"(any, config)
}
