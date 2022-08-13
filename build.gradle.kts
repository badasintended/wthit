import groovy.json.JsonGenerator
import groovy.json.JsonSlurper
import java.nio.charset.StandardCharsets

plugins {
    java
    id("org.spongepowered.gradle.vanilla")
    id("maven-publish")
}

version = env["MOD_VERSION"] ?: "${prop["majorVersion"]}.999-${env["GIT_HASH"] ?: "local"}"

allprojects {
    apply(plugin = "base")
    apply(plugin = "java")

    version = rootProject.version

    repositories {
        maven("https://maven.bai.lol")
        maven("https://dvs1.progwml6.com/files/maven/")
        maven("https://maven.shedaniel.me")
        maven("https://maven.terraformersmc.com/releases")
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        withSourcesJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.name()
        options.release.set(17)
    }

    tasks.withType<ProcessResources> {
        doLast {
            val slurper = JsonSlurper()
            val json = JsonGenerator.Options()
                .disableUnicodeEscaping()
                .build()
            fileTree(outputs.files.asPath) {
                include("**/*.json")
                forEach {
                    val mini = json.toJson(slurper.parse(it, StandardCharsets.UTF_8.name()))
                    it.writeText(mini)
                }
            }
        }
    }
}

subprojects {
    apply(plugin = "maven-publish")

    base {
        archivesName.set("${rootProject.base.archivesName.get()}-${project.name}")
    }

    publishing {
        repositories {
            maven {
                url = uri("https://maven.pkg.github.com/badasintended/wthit")
                name = "GitHub"
                credentials {
                    username = env["GITHUB_ACTOR"]
                    password = env["GITHUB_TOKEN"]
                }
            }
        }
    }
}

minecraft {
    version(rootProp["minecraft"])
}

dependencies {
    compileOnly("lol.bai:badpackets:mojmap-${rootProp["badpackets"]}")
    compileOnly("org.spongepowered:mixin:0.8.5")
}

sourceSets {
    val main by getting
    val api by creating
    val minecraftless by creating
    val mixin by creating
    val pluginCore by creating
    val pluginVanilla by creating
    val pluginTest by creating

    listOf(api, mixin, pluginCore, pluginVanilla, pluginTest).applyEach {
        compileClasspath += main.compileClasspath
    }
    listOf(main, pluginCore, pluginVanilla, pluginTest).applyEach {
        compileClasspath += api.output + mixin.output
    }
    main.apply {
        compileClasspath += minecraftless.output
    }
}

dependencies {
    val minecraftlessCompileOnly by configurations

    minecraftlessCompileOnly("com.google.code.gson:gson:2.8.9")
}
