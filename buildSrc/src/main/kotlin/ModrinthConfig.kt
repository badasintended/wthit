import com.modrinth.minotaur.TaskModrinthUpload
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.task

val PublishConfig.modrinth get() = project.run {
    apply(plugin = "com.modrinth.minotaur")

    task<TaskModrinthUpload>("modrinth") {
        dependsOn("build")

        token = env["MODRINTH_TOKEN"]
        projectId = prop["mr.projectId"]
        releaseType = prop["mr.releaseType"]

        versionNumber = "${project.name}-${project.version}"
        addLoader(project.name)

        uploadFile = tasks["remapJar"]
        additionalFiles.add(tasks.findByName("apiJar"))

        prop["mr.gameVersion"].split(", ").forEach {
            addGameVersion(it)
        }
    }
}
