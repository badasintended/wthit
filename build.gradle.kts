import groovy.json.JsonGenerator
import groovy.json.JsonSlurper
import java.nio.charset.StandardCharsets

plugins {
    java
    id("net.fabricmc.fabric-loom") version "1.19.4"
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

        mavenLocal()
    }

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(25)

        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25

        withSourcesJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.name()
        options.release = 25
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
        archivesName = "${rootProject.base.archivesName.get()}-${project.name}"
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

    compileOnly("lol.bai:badpackets:mojmap-${rootProp["badpackets"]}")
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("lol.bai:fabric-loader-environment:0.0.1")
}

sourceSets {
    val main = getByName("main")
    val api = create("api")
    val buildConst = create("buildConst")
    val minecraftless = create("minecraftless")
    val mixin = create("mixin")
    val apiPlatformStub = create("apiPlatformStub")
    val pluginCore = create("pluginCore")
    val pluginExtra = create("pluginExtra")
    val pluginHarvest = create("pluginHarvest")
    val pluginVanilla = create("pluginVanilla")
    val pluginTest = create("pluginTest")
    val test = getByName("test")

    listOf(api, buildConst, mixin, apiPlatformStub, pluginCore, pluginExtra, pluginHarvest, pluginVanilla, pluginTest).applyEach {
        compileClasspath += main.compileClasspath
    }
    listOf(api, main, mixin, pluginCore, pluginExtra, pluginHarvest, pluginVanilla, pluginTest).applyEach {
        compileClasspath += buildConst.output
    }
    listOf(main, pluginCore, pluginExtra, pluginHarvest, pluginVanilla, pluginTest).applyEach {
        compileClasspath += api.output + mixin.output + apiPlatformStub.output
    }
    api.apply {
        compileClasspath += apiPlatformStub.output
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
    val minecraftlessImplementation = configurations.getByName("minecraftlessImplementation")

    minecraftlessImplementation("com.google.code.gson:gson:2.8.9")
    minecraftlessImplementation("commons-io:commons-io:2.11.0")
    minecraftlessImplementation("org.jspecify:jspecify:1.0.0")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

loom {
    interfaceInjection.enableDependencyInterfaceInjection = false
    runs {
        configureEach {
            generateRunConfig = false
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    register<GenerateTranslationTask>("generateTranslationClass") {
        group = "translation"

        input = file("src/resources/resources/assets/waila/lang/en_us.json")
        output = file("src/buildConst/java")
        className = "mcp.mobius.waila.buildconst.Tl"
        skipPaths = setOf("waila")
    }

    named("compileBuildConstJava") {
        dependsOn("generateTranslationClass")
    }

    register<FormatTranslationTask>("formatTranslation") {
        group = "translation"

        translationDir = file("src/resources/resources/assets/waila/lang")
    }

    register<FormatTranslationTask>("validateTranslation") {
        group = "translation"

        translationDir = file("src/resources/resources/assets/waila/lang")
        test = true
    }

    listOf(generateDLIConfig).applyEach {
        configure { enabled = false }
    }
}

val apiJavadoc = tasks.register<Javadoc>("apiJavadoc") {
    group = "documentation"

    val api = sourceSets.getByName("api")
    source(api.allJava)
    classpath += api.compileClasspath
    title = "WTHIT ${prop["majorVersion"]}.x API"
    destinationDirectory = file("docs/javadoc")

    options(closureOf<StandardJavadocDocletOptions> {
        overview("src/api/javadoc/overview.html")

        links(
            "https://javadoc.io/doc/org.jetbrains/annotations/latest/",
            "https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.19.3/"
        )

        addStringOption("Xdoclint:none", "-quiet")
    })
}
