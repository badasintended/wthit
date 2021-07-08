import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.task

val PublishConfig.apiJar get() =  project.afterEvaluate {
    val remapJar: RemapJarTask by tasks
    val apiJar = task<Jar>("apiJar") {
        dependsOn(remapJar)

        archiveClassifier.set("api")

        include("fabric.mod.json")
        include("mcp/mobius/waila/api/**")

        from(zipTree(remapJar.archiveFile))
    }

    val remapSourcesJar: RemapSourcesJarTask by tasks
    val apiSourcesJar = task<Jar>("apiSourcesJar") {
        dependsOn(remapSourcesJar)

        archiveClassifier.set("api-sources")

        include("fabric.mod.json")
        include("mcp/mobius/waila/api/**")

        from(zipTree(remapSourcesJar.output))
    }

    tasks.named<Task>("build") {
        dependsOn(apiJar, apiSourcesJar)
    }
}
