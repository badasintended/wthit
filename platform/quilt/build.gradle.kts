plugins {
    id("org.quiltmc.loom") version "0.12.+"
}

setupPlatform()

dependencies {
    minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
    mappings(loom.officialMojangMappings())
    modImplementation("org.quiltmc:quilt-loader:${rootProp["quiltLoader"]}")

    modImplementation("org.quiltmc:qsl:${rootProp["qsl"]}")
    modImplementation("org.quiltmc.quilted-fabric-api:fabric-key-binding-api-v1:${rootProp["qfapi"]}")
    modImplementation("org.quiltmc.quilted-fabric-api:fabric-rendering-v1:${rootProp["qfapi"]}")

    modRuntimeOnly("lol.bai:badpackets:fabric-${rootProp["badpackets"]}")
    modRuntimeOnly("com.terraformersmc:modmenu:${rootProp["modMenu"]}")
    modRuntimeOnly("org.quiltmc.quilted-fabric-api:quilted-fabric-api:${rootProp["qfapi"]}")
}

setupStub()

sourceSets {
    val modmenu by project(":fabric").sourceSets
    main {
        compileClasspath += modmenu.output
        runtimeClasspath += modmenu.output
        resources.srcDir(rootProject.file("src/accesswidener/resources"))
    }
}

loom {
    accessWidenerPath.set(rootProject.file("src/accesswidener/resources/wthit.accesswidener"))
    runs {
        configureEach {
            isIdeConfigGenerated = true
            vmArgs += "-Dwaila.enableTestPlugin=true"
        }
    }
}

tasks.jar {
    from(project(":fabric").sourceSets["modmenu"].output)
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
