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
    implementation("me.modmuss50:mod-publish-plugin:0.5.1")

    implementation("de.undercouch:gradle-download-task:5.0.5")

    implementation("com.squareup:javapoet:1.13.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.google.guava:guava:31.0.1-jre")
}
