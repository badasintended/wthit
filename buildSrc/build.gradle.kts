plugins {
    java
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net")
    maven("https://maven.minecraftforge.net")
    maven("https://repo.spongepowered.org/repository/maven-public")
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.modrinth.minotaur:Minotaur:2.+")
    implementation("net.darkhax.curseforgegradle:CurseForgeGradle:1.0.10")

    implementation("org.spongepowered:vanillagradle:0.2.1-SNAPSHOT")
    implementation("fabric-loom:fabric-loom.gradle.plugin:0.11.+")
    implementation("net.minecraftforge.gradle:ForgeGradle:5.1.+")
    implementation("org.spongepowered:mixingradle:0.7.+")

    implementation("de.undercouch:gradle-download-task:5.0.5")
}
