import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.ReleaseType
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

fun <T : Jar> UploadConfig.curseforge(task: T) = project.run {
    apply(plugin = "me.modmuss50.mod-publish-plugin")

    configure<ModPublishExtension> {
        curseforge {
            apiEndpoint.set("https://${prop["cf.endpoint"]}")
            accessToken.set(env["CURSEFORGE_API"])
            dryRun.set(env["CURSEFORGE_API"] == null)

            projectId.set(prop["cf.projectId"])

            file.set(task.archiveFile)
            version.set("${project.name}-${project.version}")
            displayName.set("[${prop["cf.loader"]} ${rootProp["minecraft"]}] ${project.version}")
            type.set(ReleaseType.of(prop["cf.releaseType"]))

            changelog.set(env["CHANGELOG"] ?: "DRY RUN")

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
