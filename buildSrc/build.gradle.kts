plugins {
    java
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    val implementation by configurations.getting

    implementation("gradle.plugin.com.matthewprenger:CurseGradle:1.4.0")
    implementation("gradle.plugin.com.modrinth.minotaur:Minotaur:1.1.0")
}
