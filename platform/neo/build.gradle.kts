plugins {
    id("net.neoforged.gradle.userdev") version "7.0.107"
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
    create("server")
    create("client") {
        programArguments("--username", "A")
    }

    configureEach {
        workingDirectory(file("run/${namer.determineName(this)}"))

        modSource(sourceSets["main"])
        modSource(sourceSets["plugin"])
        modSource(sourceSets["dummy"])
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to project.version)
    }
}

tasks.named<JavaCompile>("compileDummyJava") {
    dependsOn(":generateTranslationClass")

    rootProject.sourceSets.filterNot { it.name == "test" }.forEach {
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
