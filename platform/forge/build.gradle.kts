import net.minecraftforge.gradle.common.util.RunConfig

plugins {
    id("net.minecraftforge.gradle").version("5.1.+")
    id("maven-publish")
}

sourceSets {
    main {
        commonProject.sourceSets.forEach {
            compileClasspath += it.output
            runtimeClasspath += it.output
        }
    }
}

repositories {
    maven("https://dvs1.progwml6.com/files/maven")
}

dependencies {
    minecraft("net.minecraftforge:forge:${rootProp["minecraft"]}-${rootProp["forge"]}")

    implementation("org.jetbrains:annotations:19.0.0")

    compileOnly(fg.deobf("mezz.jei:jei-${rootProp["minecraft"]}:${rootProp["jei"]}:api"))
    runtimeOnly(fg.deobf("mezz.jei:jei-${rootProp["minecraft"]}:${rootProp["jei"]}"))
}

minecraft {
    mappings("official", rootProp["minecraft"])
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
    runs {
        val runConfig = Action<RunConfig> {
            sources = listOf(sourceSets.main.get())
            workingDirectory(rootProject.file("run"))
            property("forge.logging.console.level", "debug")
        }
        create("client", runConfig)
        create("server", runConfig)

        val testRunConfig = Action<RunConfig> {
            runConfig(this)
            property("waila.enableTestPlugin", "true")
        }
        create("testClient", testRunConfig)
        create("testServer", testRunConfig)
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/mods.toml") {
        expand("version" to project.version)
    }
}

tasks.jar {
    fromCommonOutput()
}


tasks.sourcesJar {
    fromCommonSources()
}

afterEvaluate {
    val jar = tasks.jar.get()
    val apiJar = task<Jar>("apiJar") {
        dependsOn(jar)
        archiveClassifier.set("api")
        from(zipTree(jar.archiveFile)) {
            include("mcp/mobius/waila/api/**")
        }
    }

    val sourcesJar = tasks.sourcesJar.get()
    val apiSourcesJar = task<Jar>("apiSourcesJar") {
        dependsOn(sourcesJar)
        archiveClassifier.set("api-sources")
        from(zipTree(sourcesJar.archiveFile)) {
            include("mcp/mobius/waila/api/**")
        }
    }

    tasks.build {
        dependsOn(apiJar, apiSourcesJar)
    }

    upload {
        curseforge(jar)
        modrinth(jar)
    }

    publishing {
        repositories {
            gitlabMaven()
        }

        publications {
            create<MavenPublication>("runtime") {
                artifactId = rootProp["archiveBaseName"]
                version = "${project.name}-${project.version}"
                artifact(jar) {
                    classifier = null
                }
                artifact(sourcesJar) {
                    classifier = "sources"
                }
            }
            create<MavenPublication>("api") {
                artifactId = "${rootProp["archiveBaseName"]}-api"
                version = "${project.name}-${project.version}"
                artifact(apiJar) {
                    classifier = null
                }
                artifact(apiSourcesJar) {
                    classifier = "sources"
                }
            }
        }
    }
}
