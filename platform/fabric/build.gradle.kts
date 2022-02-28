plugins {
    id("fabric-loom")
    id("maven-publish")
}

setupPlatform()

repositories {
    maven("https://maven.bai.lol")
    maven("https://maven.shedaniel.me")
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${rootProp["fabricLoader"]}")

    //modImplementation("dev.inkwell:hermes:1.1.0+1.17")

    modCompileRuntime("net.fabricmc.fabric-api:fabric-api:${rootProp["fabricApi"]}")
    modCompileRuntime("com.terraformersmc:modmenu:${rootProp["modMenu"]}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${rootProp["rei"]}")
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:${rootProp["rei"]}")
}

sourceSets {
    val main by getting
    create("stub") {
        compileClasspath += main.compileClasspath
    }
}

loom {
    accessWidenerPath.set(file("src/main/resources/wthit.accesswidener"))
    runs {
        get("client").vmArgs += "-Dwaila.enableTestPlugin=true"
        get("server").vmArgs += "-Dwaila.enableTestPlugin=true"
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

afterEvaluate {
    val remapJar = tasks.remapJar.get()
    val apiJar = task<Jar>("apiJar") {
        dependsOn(remapJar)
        archiveClassifier.set("api")
        from(sourceSets["stub"].output)
        from(zipTree(remapJar.archiveFile)) {
            include("mcp/mobius/waila/api/**")
        }
    }

    val sourcesJar = tasks.sourcesJar.get()
    val remapSourcesJar = tasks.remapSourcesJar.get()
    val apiSourcesJar = task<Jar>("apiSourcesJar") {
        dependsOn(remapSourcesJar)
        archiveClassifier.set("api-sources")
        from(sourceSets["stub"].output)
        from(zipTree(remapSourcesJar.archiveFile)) {
            include("mcp/mobius/waila/api/**")
        }
    }

    tasks.build {
        dependsOn(apiJar, apiSourcesJar)
    }

    upload {
        curseforge(remapJar)
        modrinth(remapJar)
    }

    publishing {
        repositories {
            gitlabMaven()
        }

        publications {
            create<MavenPublication>("runtime") {
                artifactId = rootProp["archiveBaseName"]
                version = "${project.name}-${project.version}"
                artifact(remapJar) {
                    classifier = null
                }
                artifact(sourcesJar) {
                    builtBy(remapSourcesJar)
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
