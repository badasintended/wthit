pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
    }

    plugins {
        id("architectury-plugin").version("3.2-SNAPSHOT")
        id("dev.architectury.loom").version("0.7.2-SNAPSHOT")
        id("com.github.johnrengelman.shadow").version("7.0.0")
        id("com.matthewprenger.cursegradle").version("1.4.0")
        id("com.modrinth.minotaur").version("1.1.0")
    }
}

include("common")
include("fabric")
// include("forge")

rootProject.name = "wthit"
