import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

fun <A: Jar, B: Jar> UploadConfig.maven(jar: TaskProvider<A>, sourceJar: TaskProvider<B>, suffix: String? = null, action: MavenPublication.() -> Unit = {}) {
    project.extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>(suffix ?: "main") {
                artifactId = project.rootProp["archiveBaseName"] + (if (suffix != null) "-${suffix}" else "")
                version = "${project.name}-${project.version}"
                artifact(jar) {
                    classifier = null
                }
                artifact(sourceJar) {
                    classifier = "sources"
                }
                action()
            }
        }
    }
}
