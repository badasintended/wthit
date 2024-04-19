evaluationDependsOn(":textile")

plugins {
    id("org.quiltmc.loom") version "1.3.4"
}

setupPlatform()

dependencies {
    minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
    mappings(loom.officialMojangMappings())
    modImplementation("org.quiltmc:quilt-loader:${rootProp["quiltLoader"]}")

    modCompileRuntime("org.quiltmc:qsl:${rootProp["qsl"]}")
    modCompileRuntime("org.quiltmc.quilted-fabric-api:fabric-key-binding-api-v1:${rootProp["qfapi"]}")
    modCompileRuntime("org.quiltmc.quilted-fabric-api:fabric-rendering-v1:${rootProp["qfapi"]}")
    modCompileRuntime("org.quiltmc.quilted-fabric-api:fabric-lifecycle-events-v1:${rootProp["qfapi"]}")
    modCompileRuntime("org.quiltmc.quilted-fabric-api:fabric-mining-level-api-v1:${rootProp["qfapi"]}")

    modCompileRuntime("com.terraformersmc:modmenu:${rootProp["modMenu"]}")

    modRuntimeOnly("lol.bai:badpackets:fabric-${rootProp["badpackets"]}")
    modRuntimeOnly("org.quiltmc.quilted-fabric-api:quilted-fabric-api:${rootProp["qfapi"]}")
//    modRuntimeOnly("dev.architectury:architectury-fabric:${rootProp["architectury"]}")
//    modRuntimeOnly("me.shedaniel.cloth:cloth-config-fabric:${rootProp["clothConfig"]}")

    when (rootProp["recipeViewer"]) {
        "emi" -> modRuntimeOnly("dev.emi:emi-fabric:${rootProp["emi"]}")
        "rei" -> modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:${rootProp["rei"]}")
        "jei" -> rootProp["jei"].split("-").also { (mc, jei) ->
            modRuntimeOnly("mezz.jei:jei-${mc}-fabric:${jei}")
        }
    }
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
    mixin {
        add(sourceSets["main"], "wthit.refmap.json")
    }

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

    filesMatching("quilt.mod.json") {
        expand("version" to project.version)
    }
}

afterEvaluate {
    val remapJar = tasks.remapJar.get()
    val apiJar = task<ApiJarTask>("apiJar") {
        fullJar(remapJar)
    }

    val remapSourcesJar = tasks.remapSourcesJar.get()
    val apiSourcesJar = task<ApiJarTask>("apiSourcesJar") {
        fullJar(remapSourcesJar)
    }

    upload {
        curseforge(remapJar)
        modrinth(remapJar)
        maven(apiJar, apiSourcesJar, suffix = "api")
        maven(remapJar, remapSourcesJar) {
            pom.withDependencies {
                runtime("lol.bai:badpackets:fabric-${rootProp["badpackets"]}")
            }
        }
    }
}
