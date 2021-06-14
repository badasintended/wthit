plugins {
    java
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net")
    maven("https://maven.architectury.dev")
    maven("https://maven.minecraftforge.net")
}

dependencies {
    val implementation by configurations.getting

    implementation("architectury-plugin:architectury-plugin.gradle.plugin:3.2-SNAPSHOT")
    implementation("dev.architectury.loom:dev.architectury.loom.gradle.plugin:0.7.2-SNAPSHOT")
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
    implementation("gradle.plugin.com.matthewprenger:CurseGradle:1.4.0")
    implementation("gradle.plugin.com.modrinth.minotaur:Minotaur:1.1.0")
}
