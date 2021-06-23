import com.modrinth.minotaur.TaskModrinthUpload
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.task

fun Project.publishToModrinth() {
    apply(plugin = "com.modrinth.minotaur")

    task<TaskModrinthUpload>("modrinth") {
        dependsOn("build")

        token = env["MODRINTH_TOKEN"]
        projectId = prop["mr.projectId"]
        versionNumber = "${project.name}-${project.version}"
        uploadFile = tasks["remapJar"]
        releaseType = prop["mr.releaseType"]
        addLoader(project.name)

        prop["mr.gameVersion"].split(", ").forEach {
            addGameVersion(it)
        }
    }
}