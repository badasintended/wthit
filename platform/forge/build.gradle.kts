import net.minecraftforge.gradle.common.util.RunConfig
import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("net.minecraftforge.gradle")
    id("maven-publish")
}

setupPlatform()

repositories {
    maven("https://dvs1.progwml6.com/files/maven")
}

dependencies {
    minecraft("net.minecraftforge:forge:${rootProp["minecraft"]}-${rootProp["forge"]}")

    implementation("org.jetbrains:annotations:19.0.0")

    compileOnly(fg.deobf("mezz.jei:jei-${rootProp["minecraft"]}:${rootProp["jei"]}:api"))
    runtimeOnly(fg.deobf("mezz.jei:jei-${rootProp["minecraft"]}:${rootProp["jei"]}"))
}

sourceSets {
    val main by getting
    create("stub") {
        compileClasspath += main.compileClasspath
    }

    // hack to make forgegradle happy
    rootProject.sourceSets.forEach { maybeCreate(it.name) }
}

minecraft {
    mappings("official", rootProp["minecraft"])
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
    runs {
        val runConfig = Action<RunConfig> {
            workingDirectory(rootProject.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            property("waila.enableTestPlugin", "true")
            source(sourceSets["main"])
            rootProject.sourceSets.forEach { source(it) }
        }
        create("client", runConfig)
        create("server", runConfig)
    }
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
    val apiJar = task<Jar>("apiJar") {
        dependsOn(jar)
        archiveClassifier.set("api")
        from(sourceSets["stub"].output)
        from(zipTree(jar.archiveFile)) {
            include("mcp/mobius/waila/api/**")
        }
    }

    val sourcesJar = tasks.sourcesJar.get()
    val apiSourcesJar = task<Jar>("apiSourcesJar") {
        dependsOn(sourcesJar)
        archiveClassifier.set("api-sources")
        from(sourceSets["stub"].output)
        from(zipTree(sourcesJar.archiveFile)) {
            include("mcp/mobius/waila/api/**")
        }
    }

    tasks.build {
        dependsOn(apiJar, apiSourcesJar)
    }

    upload {
        curseforge(jar)
        modrinth(jar)
    }

    publishing {
        repositories {
            gitlabMaven()
        }

        publications {
            create<MavenPublication>("runtime") {
                artifactId = rootProp["archiveBaseName"]
                version = "${project.name}-${project.version}"
                artifact(jar) {
                    classifier = null
                }
                artifact(sourcesJar) {
                    classifier = "sources"
                }
            }
            create<MavenPublication>("api") {
                artifactId = "${rootProp["archiveBaseName"]}-api"
                version = "${project.name}-${project.version}"
                artifact(apiJar) {
                    classifier = null
                }
                artifact(apiSourcesJar) {
                    classifier = "sources"
                }
            }
        }
    }
}
