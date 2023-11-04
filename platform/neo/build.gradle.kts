import net.neoforged.gradle.dsl.common.runs.run.Run

plugins {
    id("net.neoforged.gradle.userdev") version "7.0.+"
}

setupPlatform()

dependencies {
    implementation("net.neoforged:neoforge:${rootProp["neo"]}")
    implementation("lol.bai:badpackets:neo-${rootProp["badpackets"]}")

    rootProp["jei"].split("-").also { (mc, jei) ->
        compileOnly("mezz.jei:jei-${mc}-common-api:${jei}")
    }
}

setupStub()

sourceSets {
    val main by getting

    val dummy by creating {
        compileClasspath += main.compileClasspath
    }
}

runs {
    val runConfig = Action<Run> {
        workingDirectory(file("run"))
        jvmArgument("-Dwaila.enableTestPlugin=true")
        jvmArgument("-Dwaila.debugCommands=true")

        modSource(sourceSets["main"])
        modSource(sourceSets["plugin"])
        modSource(sourceSets["dummy"])
    }

    create("client", runConfig)
    create("server", runConfig)
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/mods.toml") {
        expand("version" to project.version)
    }
}

tasks.named<JavaCompile>("compileDummyJava") {
    dependsOn(":generateTranslationClass")

    rootProject.sourceSets.forEach {
        source(it.allJava)
    }
}

afterEvaluate {
    val jar = tasks.jar.get()
    val apiJar = task<ApiJarTask>("apiJar") {
        fullJar(jar)
    }

    val sourcesJar = tasks.sourcesJar.get()
    val apiSourcesJar = task<ApiJarTask>("apiSourcesJar") {
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
