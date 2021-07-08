import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.architectury.plugin.ArchitectPluginExtension
import net.fabricmc.loom.LoomGradleExtension
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources

fun Project.platform(config: PlatformConfig.() -> Unit) {
    config(PlatformConfig(this))
}

@GradleDsl
class PlatformConfig(
    private val project: Project
) {

    val common get() = project.run {
        configure<ArchitectPluginExtension> {
            common(rootProp["forgeEnabled"].toBoolean())
            injectInjectables = false
        }
    }

    val fabric get() = platform("transformProductionFabric", "developmentFabric") { fabric() }
    val forge get() = platform("transformProductionForge", "developmentForge") { forge() }

    private fun platform(transformProduction: String, development: String, config: ArchitectPluginExtension.() -> Unit) = project.run {
        apply(plugin = "com.github.johnrengelman.shadow")

        configurations {
            create("shadowCommon")
        }

        configure<ArchitectPluginExtension> {
            platformSetupLoomIde()
            config(this)
        }

        configure<LoomGradleExtension> {
            useFabricMixin = true
            mixinConfig("waila.mixins.json")
        }

        dependencies {
            "implementation"(project(":common"))
            "shadowCommon"(project(path = ":common", configuration = transformProduction))
            development(project(":common"))
        }

        @Suppress("UnstableApiUsage")
        tasks.named<ProcessResources>("processResources") {
            inputs.property("version", project.version)

            filesMatching("fabric.mod.json") {
                expand("version" to project.version)
            }

            filesMatching("META-INF/mods.toml") {
                expand("version" to project.version)
            }
        }

        tasks.named<ShadowJar>("shadowJar") {
            configurations = listOf(project.configurations["shadowCommon"])
            archiveClassifier.set("dev-shadow")
        }

        tasks.named<RemapJarTask>("remapJar") {
            input.set(tasks.named<ShadowJar>("shadowJar").get().archiveFile)
            dependsOn("shadowJar")
        }

        tasks.named<Jar>("jar") {
            archiveClassifier.set("dev")
        }

        tasks.named<Jar>("sourcesJar") {
            val commonSources = project(":common").tasks.named<RemapSourcesJarTask>("remapSourcesJar").get()
            dependsOn(commonSources)
            from(zipTree(commonSources.output))
        }
    }

}
