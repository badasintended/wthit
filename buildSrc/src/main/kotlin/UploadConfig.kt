import org.gradle.api.Project
import org.gradle.kotlin.dsl.GradleDsl

fun Project.upload(config: UploadConfig.() -> Unit) {
    config(UploadConfig(this))
}

@GradleDsl
class UploadConfig(
    internal val project: Project
)
