import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.architectury.plugin.ArchitectPluginExtension
import net.fabricmc.loom.LoomGradleExtension
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources

enum class Platform(
    val transformProduction: String,
    val development: String,
    val config: ArchitectPluginExtension.() -> Unit
) {

    FABRIC("transformProductionFabric", "developmentFabric", { fabric() }),
    FORGE("transformProductionForge", "developmentForge", { forge() })

}

fun Project.platform(platform: Platform) {
    apply(plugin = "com.github.johnrengelman.shadow")

    configurations {
        create("shadowCommon")
    }

    configure<ArchitectPluginExtension> {
        platformSetupLoomIde()
        platform.config(this)
    }

    configure<LoomGradleExtension> {
        useFabricMixin = true
        mixinConfig("waila.mixins.json")
    }

    dependencies {
        "implementation"(project(":common"))
        "shadowCommon"(project(path = ":common", configuration = platform.transformProduction))
        platform.development(project(":common"))
    }

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
        classifier = "dev-shadow"
    }

    tasks.named<RemapJarTask>("remapJar") {
        input.set(tasks.named<ShadowJar>("shadowJar").get().archiveFile)
        dependsOn("shadowJar")
    }

    tasks.named<Jar>("jar") {
        classifier = "dev"
    }

    tasks.named<Jar>("sourcesJar") {
        val commonSources = project(":common").tasks.named<RemapSourcesJarTask>("remapSourcesJar").get()
        dependsOn(commonSources)
        from(zipTree(commonSources.output))
    }
}