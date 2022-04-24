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
                url = uri("https://gitlab.com/api/v4/projects/25106863/packages/maven")
                name = "GitLab"
                credentials(HttpHeaderCredentials::class) {
                    name = "Private-Token"
                    value = env["GITLAB_TOKEN"]
                }
                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }
        }
    }
}

minecraft {
    version(rootProp["minecraft"])
    accessWideners(project(":fabric").file("src/main/resources/wthit.accesswidener"))
}

dependencies {
    compileOnly("lol.bai:badpackets:mojmap-${rootProp["badpackets"]}")
}

sourceSets {
    val main by getting
    val api by creating
    val impl by creating
    val pluginCore by creating
    val pluginVanilla by creating
    val pluginTest by creating

    listOf(api, impl, pluginCore, pluginVanilla, pluginTest).applyEach {
        compileClasspath += main.compileClasspath
    }
    listOf(api, main).applyEach {
        compileClasspath += impl.output
    }
    listOf(main, pluginCore, pluginVanilla, pluginTest).applyEach {
        compileClasspath += api.output
    }
}
