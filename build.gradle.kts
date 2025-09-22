import groovy.json.JsonGenerator
import groovy.json.JsonSlurper
import java.nio.charset.StandardCharsets

plugins {
    java
    id("fabric-loom") version "1.11.8"
    id("maven-publish")
}

version = env["MOD_VERSION"] ?: "${prop["majorVersion"]}.999-${env["GIT_HASH"] ?: "local"}"

allprojects {
    apply(plugin = "base")
    apply(plugin = "java")

    version = rootProject.version

    repositories {
        maven("https://maven2.bai.lol")
        maven("https://maven.blamejared.com")
        maven("https://maven.shedaniel.me")
        maven("https://maven.terraformersmc.com/releases")

        maven("https://cursemaven.com") {
            content {
                includeGroup("curse.maven")
            }
        }

        mavenCentral {
            content {
                excludeGroupByRegex("org.lwjgl")
            }
        }

        maven("https://libraries.minecraft.net")
        maven("https://repo.spongepowered.org/repository/maven-public") {
            content {
                includeGroup("org.spongepowered")
            }
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))

        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21

        withSourcesJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.name()
        options.release.set(21)
    }

    tasks.withType<ProcessResources> {
        doLast {
            val slurper = JsonSlurper()
            val json = JsonGenerator.Options()
                .disableUnicodeEscaping()
                .build()
            fileTree(outputs.files.asPath) {
                include("**/*.json")
                forEach {
                    val mini = json.toJson(slurper.parse(it, StandardCharsets.UTF_8.name()))
                    it.writeText(mini)
                }
            }
        }
    }

    tasks.register("listPluginVersions") {
        doLast {
            project.plugins.forEach {
                println("$it -> ${it.javaClass.protectionDomain.codeSource.location.toURI().toString().lowercase()}")
            }
        }
    }
}

subprojects {
    apply(plugin = "maven-publish")

    base {
        archivesName.set("${rootProject.base.archivesName.get()}-${project.name}")
    }

    publishing {
        repositories {
            // maven {
            //     url = uri("https://maven.pkg.github.com/badasintended/wthit")
            //     name = "GitHub"
            //     credentials {
            //         username = env["GITHUB_ACTOR"]
            //         password = env["GITHUB_TOKEN"]
            //     }
            // }

            maven {
                url = uri("https://maven4.bai.lol")
                name = "Badasintended"
                credentials {
                    username = env["MAVEN_USERNAME"]
                    password = env["MAVEN_PASSWORD"]
                }
            }
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
    mappings(loom.officialMojangMappings())

    compileOnly("lol.bai:badpackets:mojmap-${rootProp["badpackets"]}")
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("lol.bai:fabric-loader-environment:0.0.1")

    rootProp["jei"].split("-").also { (mc, jei) ->
        compileOnly("mezz.jei:jei-${mc}-common-api:${jei}")
    }
}

sourceSets {
    val main by getting
    val api by creating
    val buildConst by creating
    val minecraftless by creating
    val mixin by creating
    val pluginCore by creating
    val pluginExtra by creating
    val pluginHarvest by creating
    val pluginVanilla by creating
    val pluginTest by creating
    val test by getting

    listOf(api, buildConst, mixin, pluginCore, pluginExtra, pluginHarvest, pluginVanilla, pluginTest).applyEach {
        compileClasspath += main.compileClasspath
    }
    listOf(api, main, mixin, pluginCore, pluginExtra, pluginHarvest, pluginVanilla, pluginTest).applyEach {
        compileClasspath += buildConst.output
    }
    listOf(main, pluginCore, pluginExtra, pluginHarvest, pluginVanilla, pluginTest).applyEach {
        compileClasspath += api.output + mixin.output
    }
    mixin.apply {
        compileClasspath += api.output
    }
    main.apply {
        compileClasspath += minecraftless.output
    }
    buildConst.apply {
        compiledBy("generateTranslationClass")
    }
    test.apply {
        compileClasspath -= main.output
        runtimeClasspath -= main.output
        compileClasspath += minecraftless.output + minecraftless.compileClasspath
        runtimeClasspath += minecraftless.output + minecraftless.runtimeClasspath
    }
}

dependencies {
    val minecraftlessImplementation by configurations

    minecraftlessImplementation("com.google.code.gson:gson:2.8.9")
    minecraftlessImplementation("commons-io:commons-io:2.11.0")
    minecraftlessImplementation("org.jetbrains:annotations:24.1.0")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

loom {
    interfaceInjection.enableDependencyInterfaceInjection.set(false)
    runs {
        configureEach {
            isIdeConfigGenerated = false
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    register<GenerateTranslationTask>("generateTranslationClass") {
        group = "translation"

        input.set(file("src/resources/resources/assets/waila/lang/en_us.json"))
        output.set(file("src/buildConst/java"))
        className.set("mcp.mobius.waila.buildconst.Tl")
        skipPaths.set(setOf("waila"))
    }

    named("compileBuildConstJava") {
        dependsOn("generateTranslationClass")
    }

    register<FormatTranslationTask>("formatTranslation") {
        group = "translation"

        translationDir.set(file("src/resources/resources/assets/waila/lang"))
    }

    register<FormatTranslationTask>("validateTranslation") {
        group = "translation"

        translationDir.set(file("src/resources/resources/assets/waila/lang"))
        test.set(true)
    }

    listOf(remapJar, remapSourcesJar, generateRemapClasspath, generateDLIConfig).applyEach {
        configure { enabled = false }
    }
}

val apiJavadoc by tasks.registering(Javadoc::class) {
    group = "documentation"

    val api by sourceSets
    source(api.allJava)
    classpath += api.compileClasspath
    title = "WTHIT ${prop["majorVersion"]}.x API"
    setDestinationDir(file("docs/javadoc"))

    options(closureOf<StandardJavadocDocletOptions> {
        overview("src/api/javadoc/overview.html")

        links(
            "https://javadoc.io/doc/org.jetbrains/annotations/latest/",
            "https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.19.3/"
        )

        addStringOption("Xdoclint:none", "-quiet")
    })
}

subprojects {
    afterEvaluate {
        val subApi = sourceSets.findByName("api")

        if (subApi != null) {
            apiJavadoc.get().source(subApi.allJava)
            apiJavadoc.get().classpath += subApi.compileClasspath
        }
    }
}
