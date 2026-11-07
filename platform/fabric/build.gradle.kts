evaluationDependsOn(":textile")

plugins {
    id("net.fabricmc.fabric-loom")
}

setupPlatform()

dependencies {
    minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
    implementation("net.fabricmc:fabric-loader:${rootProp["fabricLoader"]}")
    compileRuntime("net.fabricmc.fabric-api:fabric-api:${rootProp["fabricApi"]}")
    runtimeOnly("lol.bai:badpackets:fabric-${rootProp["badpackets"]}")
}

setupStub()

sourceSets {
    val textileSourceSets = project(":textile").sourceSets
    val main by getting
    val plugin by getting

    main {
        compileClasspath += textileSourceSets["main"].output
        runtimeClasspath += textileSourceSets["main"].output
    }

    plugin.apply {
        compileClasspath += textileSourceSets["plugin"].output
    }

    listOf(main, plugin).applyEach {
        runtimeClasspath += textileSourceSets["plugin"].output
    }
}

loom {
    interfaceInjection.enableDependencyInterfaceInjection.set(false)

    runs {
        getByName("client") {
            programArgs("--username", "A")
        }

        configureEach {
            isIdeConfigGenerated = true
            runDir = "run/${namer.determineName(this)}"
        }
    }
}

tasks.jar {
    val textileSourceSets = project(":textile").sourceSets
    from(textileSourceSets["main"].output)
    from(textileSourceSets["api"].output)
    from(textileSourceSets["plugin"].output)
}

tasks.sourcesJar {
    val textileSourceSets = project(":textile").sourceSets
    from(textileSourceSets["main"].allSource)
    from(textileSourceSets["api"].allSource)
    from(textileSourceSets["plugin"].allSource)
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

afterEvaluate {
    val jar = tasks.jar
    val apiJar by tasks.registering(ApiJarTask::class) {
        fullJar(jar)
    }

    val sourcesJar = tasks.sourcesJar
    val apiSourcesJar by tasks.registering(ApiJarTask::class) {
        fullJar(sourcesJar)
    }

    upload {
        curseforge(jar)
        modrinth(jar)
        maven(apiJar, apiSourcesJar, suffix = "api")
        maven(jar, sourcesJar) {
            pom.withDependencies {
                runtime("lol.bai:badpackets:fabric-${rootProp["badpackets"]}")
            }
        }
    }
}
