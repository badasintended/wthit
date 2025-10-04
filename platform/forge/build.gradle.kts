plugins {
    id("net.minecraftforge.gradle") version "6.0.44"
    id("org.spongepowered.mixin") version "0.7.38"
}

setupPlatform(setRuntimeClasspath = false)

dependencies {
    minecraft("net.minecraftforge:forge:${rootProp["minecraft"]}-${rootProp["forge"]}")
    annotationProcessor("net.minecraftforge:eventbus-validator:7.0-beta.7")

    implementation("org.jetbrains:annotations:19.0.0")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    runtimeOnly("lol.bai:badpackets:forge-${rootProp["badpackets"]}")
//    runtimeOnly(fg.deobf("dev.architectury:architectury-forge:${rootProp["architectury"]}"))
//    runtimeOnly(fg.deobf("me.shedaniel.cloth:cloth-config-forge:${rootProp["clothConfig"]}"))

    // https://www.curseforge.com/minecraft/mc-mods/travelers-backpack/files/4584396
//    runtimeOnly(fg.deobf("curse.maven:travelers-backpack-321117:4584396"))

    when (rootProp["recipeViewer"]) {
        "rei" -> {
            runtimeOnly(fg.deobf("me.shedaniel:RoughlyEnoughItems-forge:${rootProp["rei"]}"))
//            runtimeOnly(fg.deobf("me.shedaniel:RoughlyEnoughItems-plugin-compatibilities-forge:${rootProp["rei"]}"))
        }

        "jei" -> rootProp["jei"].split("-").also { (mc, jei) ->
            runtimeOnly(fg.deobf("mezz.jei:jei-${mc}-forge:${jei}"))
        }
    }

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
    val excluded = setOf("run", "stub", "test")

    sourceSets.filterNot { excluded.contains(it.name) }.forEach { source(it.allJava) }
    rootProject.sourceSets.filterNot { excluded.contains(it.name) }.forEach { source(it.allJava) }
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
            property("eventbus.api.strictRuntimeChecks", "true")

            sources = listOf(sourceSets["run"])
        }
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
                runtime("lol.bai:badpackets:forge-${rootProp["badpackets"]}")
            }
        }
    }
}
