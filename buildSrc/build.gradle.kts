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

    // fixes clashes with loom's guava that makes access widener fails to be read
    implementation("com.google.guava:guava:31.0.1-jre")
}
