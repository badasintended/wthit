import org.gradle.api.Project
import org.gradle.kotlin.dsl.GradleDsl

fun Project.publish(config: PublishConfig.() -> Unit) {
    config(PublishConfig(this))
}

@GradleDsl
class PublishConfig(
    internal val project: Project
)
