import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.compileRuntime(any: String, config: ExternalModuleDependency.() -> Unit = {}) {
    "compileOnly"(any, config)
    "runtimeOnly"(any, config)
}
