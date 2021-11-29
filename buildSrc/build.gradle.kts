plugins {
    java
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net")
    maven("https://maven.minecraftforge.net")
}

dependencies {
    implementation("gradle.plugin.com.modrinth.minotaur:Minotaur:1.1.0")
    implementation("gradle.plugin.com.matthewprenger:CurseGradle:1.4.0")
    implementation("fabric-loom:fabric-loom.gradle.plugin:0.10.+")
    implementation("net.minecraftforge.gradle:ForgeGradle:5.1.+")
}
