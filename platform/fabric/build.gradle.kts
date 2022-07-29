plugins {
    id("fabric-loom") version "0.12.+"
}

setupPlatform()

dependencies {
    minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${rootProp["fabricLoader"]}")

    modCompileRuntime("net.fabricmc.fabric-api:fabric-api:${rootProp["fabricApi"]}")
    modCompileRuntime("com.terraformersmc:modmenu:${rootProp["modMenu"]}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${rootProp["rei"]}")
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:${rootProp["rei"]}")

    modRuntimeOnly("lol.bai:badpackets:fabric-${rootProp["badpackets"]}")
}

setupStub()

sourceSets {
    val main by getting
    val integration by creating {
        compileClasspath += main.compileClasspath
    }
    main {
        compileClasspath += integration.output
        runtimeClasspath += integration.output
    }
}

loom {
    interfaceInjection.enableDependencyInterfaceInjection.set(false)

    mixin {
        add(sourceSets["main"], "wthit.refmap.json")
    }

    runs {
        configureEach {
            isIdeConfigGenerated = true
            vmArgs += "-Dwaila.enableTestPlugin=true"
        }
    }
}

tasks.jar {
    from(sourceSets["integration"].output)
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
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
