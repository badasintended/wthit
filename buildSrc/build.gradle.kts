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
}

dependencies {
    implementation("gradle.plugin.com.modrinth.minotaur:Minotaur:1.1.0")
    implementation("gradle.plugin.com.matthewprenger:CurseGradle:1.4.0")

    implementation("org.spongepowered:vanillagradle:0.2.1-SNAPSHOT")
    implementation("fabric-loom:fabric-loom.gradle.plugin:0.11.+")
    implementation("net.minecraftforge.gradle:ForgeGradle:5.1.+")
    implementation("org.spongepowered:mixingradle:0.7.+")
}
