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
    val main = getByName("main")
    val plugin = getByName("plugin")

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
    interfaceInjection.enableDependencyInterfaceInjection = false

    runs {
        getByName("client") {
            programArguments.addAll("--username", "A")
        }

        configureEach {
            generateRunConfig = true
            runDirectory = layout.projectDirectory.dir("run/${namer.determineName(this)}")
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
    val apiJar = tasks.register<ApiJarTask>("apiJar") {
        fullJar(jar)
    }

    val sourcesJar = tasks.sourcesJar
    val apiSourcesJar = tasks.register<ApiJarTask>("apiSourcesJar") {
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
