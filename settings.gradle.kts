pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net")
        maven("https://maven.minecraftforge.net")
    }

    plugins {
        id("com.matthewprenger.cursegradle").version("1.4.0")
        id("com.modrinth.minotaur").version("1.1.0")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "net.minecraftforge.gradle") {
                useModule("${requested.id}:ForgeGradle:${requested.version}")
            }
        }
    }
}

include("common")
include("fabric")
include("forge")

rootProject.name = "wthit"
