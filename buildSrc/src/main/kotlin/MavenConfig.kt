import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

fun UploadConfig.mavenApi(jar: Jar, sourceJar: Jar) {
    maven("api", "${project.rootProp["archiveBaseName"]}-api", jar, sourceJar)
}

fun UploadConfig.mavenRuntime(jar: Jar, sourceJar: Jar) {
    maven("runtime", project.rootProp["archiveBaseName"], jar, sourceJar)
}

private fun UploadConfig.maven(name: String, id: String, jar: Jar, sourceJar: Jar) {
    project.extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>(name) {
                artifactId = id
                version = "${project.name}-${project.version}"
                artifact(jar) {
                    classifier = null
                }
                artifact(sourceJar) {
                    classifier = "sources"
                }
            }
        }
    }
}
