plugins {
    id("fabric-loom").version("0.8-SNAPSHOT")
    id("maven-publish")
}

repositories {
    maven("https://bai.jfrog.io/artifactory/maven")
    maven("https://maven.architectury.dev")
}

sourceSets {
    main {
        compileClasspath += commonProject.sourceSets["main"].output
        runtimeClasspath += commonProject.sourceSets["main"].output
        resources.srcDir(commonProject.sourceSets["main"].resources.srcDirs)
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${rootProp["fabricLoader"]}")

    modImplementation("dev.inkwell:hermes:1.1.0+1.17")

    modCompileRuntime("net.fabricmc.fabric-api:fabric-api:${rootProp["fabricApi"]}")
    modCompileRuntime("com.terraformersmc:modmenu:${rootProp["modMenu"]}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${rootProp["rei"]}")
    modRuntime("me.shedaniel:RoughlyEnoughItems-fabric:${rootProp["rei"]}")
}

loom {
    accessWidener = file("src/main/resources/wthit.accesswidener")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.jar {
    from(commonProject.sourceSets.main.get().output.classesDirs)
}

tasks.sourcesJar {
    from(commonProject.sourceSets.main.get().allJava)
}

afterEvaluate {
    val remapJar = tasks.remapJar.get()
    val apiJar = task<Jar>("apiJar") {
        dependsOn(remapJar)
        archiveClassifier.set("api")
        include("fabric.mod.json")
        include("mcp/mobius/waila/api/**")
        from(zipTree(remapJar.archiveFile))
    }

    val sourcesJar = tasks.sourcesJar.get()
    val remapSourcesJar = tasks.remapSourcesJar.get()
    val apiSourcesJar = task<Jar>("apiSourcesJar") {
        dependsOn(remapSourcesJar)
        archiveClassifier.set("api-sources")
        include("fabric.mod.json")
        include("mcp/mobius/waila/api/**")
        from(zipTree(remapSourcesJar.output))
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
