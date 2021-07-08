import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseExtension
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.*

val PublishConfig.curseforge get() = project.run {
    apply(plugin = "com.matthewprenger.cursegradle")

    val remapJar: RemapJarTask by tasks
    env["CURSEFORGE_API"]?.let { CURSEFORGE_API ->
        configure<CurseExtension> {
            apiKey = CURSEFORGE_API
            project(closureOf<CurseProject> {
                id = prop["cf.projectId"]
                releaseType = prop["cf.releaseType"]

                changelogType = "markdown"
                changelog = "https://github.com/badasintended/wthit/releases/tag/${project.version}"

                addGameVersion(project.name.capitalize())
                prop["cf.gameVersion"].split(", ").forEach(this::addGameVersion)

                mainArtifact(remapJar, closureOf<CurseArtifact> {
                    displayName = "[${rootProp["minecraft"]}] ${project.version}"
                })

                relations(closureOf<CurseRelation> {
                    prop.ifPresent("cf.require") {
                        it.split(", ").forEach(this::requiredDependency)
                    }
                    prop.ifPresent("cf.optional") {
                        it.split(", ").forEach(this::optionalDependency)
                    }
                    prop.ifPresent("cf.break") {
                        it.split(", ").forEach(this::incompatible)
                    }
                })

                afterEvaluate {
                    uploadTask.dependsOn("build")
                }
            })
        }
    }
}
