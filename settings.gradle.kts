pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net")
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.quiltmc.org/repository/release")
        maven("https://repo.spongepowered.org/repository/maven-public")
    }

    resolutionStrategy.eachPlugin {
        when (requested.id.id) {
            "org.spongepowered.mixin" -> useModule("org.spongepowered:mixingradle:${requested.version}")
        }
    }
}

rootProject.name = "wthit"

fun platform(name: String) {
    include(name)
    project(":${name}").projectDir = file("platform/${name}")
}

//platform("bukkit")
//platform("fabric")
//platform("forge")
//platform("mojmap")
//platform("textile")
//platform("quilt")
