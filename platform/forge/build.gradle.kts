import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("net.minecraftforge.gradle") version "6.0.21"
    id("org.spongepowered.mixin") version "0.7.38"
}

setupPlatform()

dependencies {
    minecraft("net.neoforged:forge:${rootProp["minecraft"]}-${rootProp["forge"]}")

    implementation("org.jetbrains:annotations:19.0.0")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    runtimeOnly(fg.deobf("lol.bai:badpackets:forge-${rootProp["badpackets"]}"))
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
}

setupStub()

sourceSets {
    // hack to make forgegradle happy
    rootProject.sourceSets.forEach {
        if (findByName(it.name) == null) {
            create(it.name) {
                java.setSrcDirs(emptyList<Any>())
                resources.setSrcDirs(emptyList<Any>())
            }
        }
    }
}

minecraft {
    mappings("official", rootProp["minecraft"])
    runs {
        create("server")
        create("client") {
            args("--username", "A")
        }

        configureEach {
            workingDirectory(file("run/${namer.determineName(this)}"))
            ideaModule("${rootProject.name}.${project.name}.main")

            source(sourceSets["main"])
            source(sourceSets["api"])
            source(sourceSets["plugin"])
            rootProject.sourceSets.forEach { source(it) }
        }
    }
}

mixin {
    add(sourceSets["main"], "wthit.refmap.json")
    config("wthit.mixins.json")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/mods.toml") {
        expand("version" to project.version)
    }
}

tasks.jar {
    manifest.attributes(
        "Specification-Title" to "WAILA",
        "Specification-Vendor" to "ProfMobius",
        "Specification-Version" to "1",
        "Implementation-Title" to rootProject.name,
        "Implementation-Version" to rootProject.version,
        "Implementation-Vendor" to "deirn, TehNut, ProfMobius",
        "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
        "Automatic-Module-Name" to "mcp.mobius.waila"
    )

    finalizedBy("reobfJar")
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
