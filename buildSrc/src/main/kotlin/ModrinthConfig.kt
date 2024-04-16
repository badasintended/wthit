import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.ReleaseType
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

fun <T : Jar> UploadConfig.modrinth(task: T) = project.run {
    apply(plugin = "me.modmuss50.mod-publish-plugin")

    configure<ModPublishExtension> {
        modrinth {
            accessToken.set(env["MODRINTH_TOKEN"])
            dryRun.set(env["MODRINTH_TOKEN"] == null)

            projectId.set(prop["mr.projectId"])
            file.set(task.archiveFile)
            displayName.set("${project.version}")
            version.set("${project.name}-${project.version}")
            type.set(ReleaseType.of(prop["mr.releaseType"]))
            changelog.set(env["CHANGELOG"] ?: "DRY RUN")

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
