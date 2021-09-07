import org.gradle.api.Project

val env: Map<String, String> get() = System.getenv()

inline fun <T> Iterable<T>.applyEach(action: T.() -> Unit) {
    forEach(action)
}

val Project.mavenVersion get() = "${projectDir.name}-${version}"
