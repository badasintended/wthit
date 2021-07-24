import com.modrinth.minotaur.TaskModrinthUpload
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.task

fun <T : Jar> UploadConfig.modrinth(task: T) = project.run {
    apply(plugin = "com.modrinth.minotaur")

    task<TaskModrinthUpload>("modrinth") {
        dependsOn("build")

        token = env["MODRINTH_TOKEN"]
        projectId = prop["mr.projectId"]
        releaseType = prop["mr.releaseType"]

        versionNumber = "${project.name}-${project.version}"
        addLoader(project.name)

        uploadFile = task

        prop["mr.gameVersion"].split(", ").forEach {
            addGameVersion(it)
        }
    }
}
