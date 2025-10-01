plugins {
    id("net.neoforged.moddev") version "2.0.110"
}

setupPlatform(setRuntimeClasspath = false)

dependencies {
    implementation("lol.bai:badpackets:neo-${rootProp["badpackets"]}")

    rootProp["jei"].split("-").also { (mc, jei) ->
        compileOnly("mezz.jei:jei-${mc}-common-api:${jei}")
    }
}

setupStub()

neoForge {
    version = rootProp["neo"]

    runs {
        create("server") { server() }
        create("client") {
            client()
            programArguments.addAll("--username", "A")
        }

        configureEach {
            gameDirectory = file("run/${namer.determineName(this)}")
        }
    }

    mods {
        create("wthit") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["plugin"])

            val excluded = setOf("test")
            rootProject.sourceSets.filterNot { excluded.contains(it.name) }.forEach { sourceSet(it) }
        }
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
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
                runtime("lol.bai:badpackets:neo-${rootProp["badpackets"]}")
            }
        }
    }
}
