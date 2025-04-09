import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.ReleaseType
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure

fun <T : Jar> UploadConfig.curseforge(task: TaskProvider<T>) = project.run {
    apply(plugin = "me.modmuss50.mod-publish-plugin")

    configure<ModPublishExtension> {
        curseforge {
            apiEndpoint = "https://${prop["cf.endpoint"]}"
            accessToken = env["CURSEFORGE_API"]
            dryRun = env["CURSEFORGE_API"] == null

            projectId = prop["cf.projectId"]

            file = task.get().archiveFile
            version = "${project.name}-${project.version}"
            displayName = "[${prop["cf.loader"]} ${rootProp["minecraft"]}] ${project.version}"
            type = ReleaseType.of(prop["cf.releaseType"])

            changelog = env["CHANGELOG"] ?: "DRY RUN"

            minecraftVersions.addAll(prop["cf.gameVersion"].split(", "))
            modLoaders.addAll(prop["cf.loader"].split(", "))

            fun relation(key: String, fn: (Array<String>) -> Unit) {
                prop.ifPresent("cf.${key}") { value ->
                    fn(value.split(", ").toTypedArray())
                }
            }

            relation("require", this::requires)
            relation("optional", this::optional)
            relation("break", this::incompatible)
        }
    }
}
