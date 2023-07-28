import net.darkhax.curseforgegradle.Constants
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create

fun <T : Jar> UploadConfig.curseforge(task: T) = project.run {
    apply(plugin = "net.darkhax.curseforgegradle")

    tasks.create<TaskPublishCurseForge>("curseforge") {
        group = "publishing"
        dependsOn("build")

        disableVersionDetection()

        apiToken = env["CURSEFORGE_API"]
        apiEndpoint = "https://${prop["cf.endpoint"]}"

        upload(prop["cf.projectId"], task).apply {
            displayName = "[${project.name.capitalize()} ${rootProp["minecraft"]}] ${project.version}"
            releaseType = prop["cf.releaseType"]

            changelogType = "markdown"
            changelog = env["CHANGELOG"]

            prop["cf.gameVersion"].split(", ").forEach(this::addGameVersion)

            prop["cf.loader"].split(", ").forEach {
                addModLoader(it)
            }

            fun relation(key: String, type: String) {
                prop.ifPresent("cf.${key}") { value ->
                    value.split(", ").forEach {
                        addRelation(it, type)
                    }
                }
            }

            relation("require", Constants.RELATION_REQUIRED);
            relation("optional", Constants.RELATION_OPTIONAL);
            relation("break", Constants.RELATION_INCOMPATIBLE);
        }
    }
}
