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

        apiToken = env["CURSEFORGE_API"]
        apiEndpoint = "https://${prop["cf.endpoint"]}"

        upload(prop["cf.projectId"], task).apply {
            displayName = "[${rootProp["minecraft"]}] ${project.version}"
            releaseType = prop["cf.releaseType"]

            changelogType = "markdown"
            changelog = "https://github.com/badasintended/wthit/releases/tag/${project.version}"

            prop["cf.gameVersion"].split(", ").forEach(this::addGameVersion)

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
