import com.modrinth.minotaur.ModrinthExtension
import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

fun <T : Jar> UploadConfig.modrinth(task: T) = project.run {
    apply(plugin = "com.modrinth.minotaur")

    env["MODRINTH_TOKEN"]?.let { MODRINTH_TOKEN ->
        configure<ModrinthExtension> {
            token.set(MODRINTH_TOKEN)
            projectId.set(prop["mr.projectId"])

            versionNumber.set("${project.name}-${project.version}")
            versionName.set("${project.version}")
            versionType.set(prop["mr.releaseType"])
            changelog.set(env["CHANGELOG"])

            uploadFile.set(task)

            loaders.add(project.name)
            gameVersions.addAll(prop["mr.gameVersion"].split(", "))

            fun dependency(key: String, type: DependencyType) {
                prop.ifPresent("mr.${key}") { value ->
                    value.split(", ").forEach {
                        dependencies.add(ModDependency(it, type))
                    }
                }
            }

            dependency("require", DependencyType.REQUIRED)
            dependency("optional", DependencyType.OPTIONAL)
            dependency("break", DependencyType.INCOMPATIBLE)
        }
    }
}
