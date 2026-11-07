plugins {
    id("net.minecraftforge.gradle") version "6.0.25"
    id("org.spongepowered.mixin") version "0.7.38"
}

setupPlatform(setRuntimeClasspath = false)

dependencies {
    minecraft("net.minecraftforge:forge:${rootProp["minecraft"]}-${rootProp["forge"]}")

    implementation("org.jetbrains:annotations:19.0.0")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    runtimeOnly("lol.bai:badpackets:forge-${rootProp["badpackets"]}")

    implementation("net.sf.jopt-simple:jopt-simple:5.0.4") { version { strictly("5.0.4") } }
}

setupStub()

sourceSets {
    val main by getting
    val run by creating {
        java.setSrcDirs(emptyList<Any>())
        resources.setSrcDirs(emptyList<Any>())

        compileClasspath += main.compileClasspath + rootProject.sourceSets.main.get().compileClasspath
        runtimeClasspath += main.runtimeClasspath - main.output

        val dir = layout.buildDirectory.dir("run")
        java.destinationDirectory = dir
        output.setResourcesDir(dir)
    }
}

tasks.named<JavaCompile>("compileRunJava") {
    val excluded = setOf("run", "stub", "test", "apiPlatformStub")

    sourceSets.filterNot { excluded.contains(it.name) }.forEach { source(it.allJava) }
    rootProject.sourceSets.filterNot { excluded.contains(it.name) }.forEach { source(it.allJava) }

    source(rootProject.sourceSets["apiPlatformStub"].allJava.filterNot { it.path.contains("minecraftforge") })
}

tasks.named<ProcessResources>("processRunResources") {
    val excluded = setOf("run", "stub", "test")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    sourceSets.filterNot { excluded.contains(it.name) }.forEach { from(it.resources) }
    rootProject.sourceSets.filterNot { excluded.contains(it.name) }.forEach { from(it.resources) }
}

minecraft {
    mappings("official", rootProp["minecraft"])
    reobf = false

    runs {
        create("server")
        create("client") {
            args("--username", "A")
        }

        configureEach {
            workingDirectory(file("run/${namer.determineName(this)}"))
            ideaModule("${rootProject.name}.${project.name}.run")

            sources = listOf(sourceSets["run"])
        }
    }
}

afterEvaluate {
    minecraft.runs.configureEach {
        jvmArgs("-classpath", sourceSets["run"].runtimeClasspath.asPath)
    }
}

mixin {
    add(sourceSets["main"], "wthit.refmap.json")
    config("wthit.mixins.json")
}

tasks.jar {
    manifest.attributes(mapOf(
        "MixinConfigs" to "wthit.mixins.json"
    ))
}

tasks.withType<ProcessResources> {
    inputs.property("version", project.version)

    filesMatching("META-INF/mods.toml") {
        expand("version" to project.version)
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
                runtime("lol.bai:badpackets:forge-${rootProp["badpackets"]}")
            }
        }
    }
}
