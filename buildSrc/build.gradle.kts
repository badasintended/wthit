plugins {
    java
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://jitpack.io")

    // https://github.com/MinecraftForge/ForgeGradle/issues/919
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    implementation("me.modmuss50:mod-publish-plugin:0.5.1")

    implementation("de.undercouch:gradle-download-task:5.0.5")

    implementation("com.squareup:javapoet:1.13.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.google.guava:guava:31.0.1-jre")

    // https://github.com/MinecraftForge/ForgeGradle/issues/919
    implementation("net.minecraftforge:srgutils:0.5.10")
}
