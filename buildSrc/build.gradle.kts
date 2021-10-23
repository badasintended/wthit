plugins {
    java
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("gradle.plugin.com.modrinth.minotaur:Minotaur:1.1.0")
    implementation("gradle.plugin.com.matthewprenger:CurseGradle:1.4.0") {
        exclude(group = "com.google.guava")
    }
}
