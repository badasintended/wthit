plugins {
    java
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.modrinth.minotaur:Minotaur:2.2.1")
    implementation("com.github.deirn:CurseForgeGradle:c693018f92")

    implementation("de.undercouch:gradle-download-task:5.0.5")
}
