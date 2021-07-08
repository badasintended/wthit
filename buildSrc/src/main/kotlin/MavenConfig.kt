import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.authentication.http.HttpHeaderAuthentication
import org.gradle.kotlin.dsl.*

val PublishConfig.maven get() = project.run {
    apply(plugin = "maven-publish")

    afterEvaluate {
        val remapJar: RemapJarTask by tasks
        val sourcesJar: Jar by tasks
        val remapSourcesJar: RemapSourcesJarTask by tasks

        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("runtime") {
                    artifactId = rootProp["archiveBaseName"]
                    version = "${project.name}-${project.version}"
                    artifact(remapJar) {
                        classifier = null
                    }
                    artifact(sourcesJar) {
                        builtBy(remapSourcesJar)
                        classifier = "sources"
                    }
                }
                val apiJar = tasks.findByName("apiJar")
                if (apiJar != null) create<MavenPublication>("api") {
                    artifactId = "${rootProp["archiveBaseName"]}-api"
                    version = "${project.name}-${project.version}"
                    artifact(apiJar) {
                        classifier = null
                    }
                    artifact(tasks["apiSourcesJar"]) {
                        classifier = "sources"
                    }
                }
            }
            repositories {
                maven {
                    url = uri("https://gitlab.com/api/v4/projects/25106863/packages/maven")
                    name = "GitLab"
                    credentials(HttpHeaderCredentials::class) {
                        name = "Private-Token"
                        value = env["GITLAB_TOKEN"]
                    }
                    authentication {
                        create<HttpHeaderAuthentication>("header")
                    }
                }
            }
        }
    }
}
