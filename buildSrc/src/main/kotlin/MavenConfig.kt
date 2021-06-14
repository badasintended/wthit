import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.authentication.http.HttpHeaderAuthentication
import org.gradle.kotlin.dsl.*

fun Project.publishToMaven() {
    apply(plugin = "maven-publish")

    afterEvaluate {
        val remapJar: RemapJarTask by tasks
        val apiJar = task<Jar>("apiJar") {
            dependsOn(remapJar)

            classifier = "api"

            include("fabric.mod.json")
            include("mcp/mobius/waila/api/**")

            from(zipTree(remapJar.archiveFile))
        }

        val sourcesJar: Jar by tasks
        val remapSourcesJar: RemapSourcesJarTask by tasks
        val apiSourcesJar = task<Jar>("apiSourcesJar") {
            dependsOn(remapSourcesJar)

            classifier = "api-sources"

            include("fabric.mod.json")
            include("mcp/mobius/waila/api/**")

            from(zipTree(remapSourcesJar.output))
        }

        tasks.named<Task>("build") {
            dependsOn(apiJar, apiSourcesJar)
        }

        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("runtime") {
                    artifactId = rootProp["archiveBaseName"]
                    version = "${project.name}-${project.version}"
                    artifact(remapJar) {
                        classifier = null
                    }
                    artifact(sourcesJar).apply {
                        builtBy(remapSourcesJar)
                    }
                }
                create<MavenPublication>("api") {
                    artifactId = "${rootProp["archiveBaseName"]}-api"
                    version = "${project.name}-${project.version}"
                    artifact(apiJar) {
                        classifier = null
                    }
                    artifact(apiSourcesJar) {
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
