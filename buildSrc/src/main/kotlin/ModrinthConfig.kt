import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.ReleaseType
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure

fun <T : Jar> UploadConfig.modrinth(task: TaskProvider<T>) = project.run {
    apply(plugin = "me.modmuss50.mod-publish-plugin")

    configure<ModPublishExtension> {
        modrinth {
            accessToken = env["MODRINTH_TOKEN"]
            dryRun = env["MODRINTH_TOKEN"] == null

            projectId = prop["mr.projectId"]
            file = task.get().archiveFile
            displayName = "${project.version}"
            version = "${project.name}-${project.version}"
            type = ReleaseType.of(prop["mr.releaseType"])
            changelog = env["CHANGELOG"] ?: "DRY RUN"

            modLoaders.addAll(prop["mr.loader"].split(", "))
            minecraftVersions.addAll(prop["mr.gameVersion"].split(", "))

            fun relation(key: String, fn: (Array<String>) -> Unit) {
                prop.ifPresent("mr.${key}") { value ->
                    fn(value.split(", ").toTypedArray())
                }
            }

            relation("require", this::requires)
            relation("optional", this::optional)
            relation("break", this::incompatible)
        }
    }
}
