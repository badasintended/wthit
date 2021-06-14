import com.modrinth.minotaur.TaskModrinthUpload
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

fun Project.publishToModrinth(loader: String) {
    apply(plugin = "com.modrinth.minotaur")

    task<TaskModrinthUpload>("modrinth") {
        dependsOn("build")

        token = env["MODRINTH_TOKEN"]
        projectId = prop["mr.projectId"]
        versionNumber = version.toString()
        uploadFile = tasks["remapJar"]
        releaseType = prop["mr.releaseType"]
        addLoader(loader)

        prop["mr.gameVersion"].split(", ").forEach {
            addGameVersion(it)
        }
    }
}