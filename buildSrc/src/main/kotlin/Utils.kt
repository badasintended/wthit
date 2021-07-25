import org.gradle.api.Project

val env: Map<String, String> get() = System.getenv()

val Project.commonProject get() = project(":common")
