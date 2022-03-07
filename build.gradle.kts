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

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        withSourcesJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.name()
        options.release.set(17)
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
