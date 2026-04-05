import net.minecraftforge.gradle.internal.SlimeLauncherRunTask

plugins {
    id("net.minecraftforge.gradle") version "[7.0.11,8.0)"
}

setupPlatform(setRuntimeClasspath = false)

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:${rootProp["minecraft"]}-${rootProp["forge"]}"))
    annotationProcessor("net.minecraftforge:eventbus-validator:7.0-beta.7")

    implementation("org.jetbrains:annotations:19.0.0")

    runtimeOnly("lol.bai:badpackets:forge-${rootProp["badpackets"]}")
//    runtimeOnly("dev.architectury:architectury-forge:${rootProp["architectury"]}")
//    runtimeOnly("me.shedaniel.cloth:cloth-config-forge:${rootProp["clothConfig"]}")

    // https://www.curseforge.com/minecraft/mc-mods/travelers-backpack/files/4584396
//    runtimeOnly("curse.maven:travelers-backpack-321117:4584396")

    when (rootProp["recipeViewer"]) {
        "rei" -> {
            runtimeOnly("me.shedaniel:RoughlyEnoughItems-forge:${rootProp["rei"]}")
//            runtimeOnly("me.shedaniel:RoughlyEnoughItems-plugin-compatibilities-forge:${rootProp["rei"]}")
        }

        "jei" -> rootProp["jei"].split("-").also { (mc, jei) ->
            runtimeOnly("mezz.jei:jei-${mc}-forge:${jei}")
        }
    }
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
    dependsOn(":generateTranslationClass")

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

    runs {
        create("server")
        create("client") {
            args("--username", "A")
            args("--mixin.config", "wthit.mixins.json")
        }

        configureEach {
            workingDir = file("run/${namer.determineName(this)}")
        }
    }
}

afterEvaluate {
    tasks.withType<SlimeLauncherRunTask> {
        if (this is JavaExec) {
            sourceSetName = "run"
            classpath -= sourceSets["main"].runtimeClasspath
            classpath += sourceSets["run"].runtimeClasspath
        }
    }
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
