import groovy.json.JsonGenerator
import groovy.json.JsonSlurper
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL
import java.nio.charset.StandardCharsets

plugins {
    java
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.7.20"
}

version = env["MOD_VERSION"] ?: "${prop["majorVersion"]}.999-${env["GIT_HASH"] ?: "local"}"

allprojects {
    apply(plugin = "base")
    apply(plugin = "java")

    version = rootProject.version

    repositories {
        maven("https://maven.bai.lol")
        maven("https://dvs1.progwml6.com/files/maven/")
        maven("https://maven.shedaniel.me")
        maven("https://maven.terraformersmc.com/releases")
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        withSourcesJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.name()
        options.release.set(17)
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
}

subprojects {
    apply(plugin = "maven-publish")

    base {
        archivesName.set("${rootProject.base.archivesName.get()}-${project.name}")
    }

    publishing {
        repositories {
            maven {
                url = uri("https://maven.pkg.github.com/badasintended/wthit")
                name = "GitHub"
                credentials {
                    username = env["GITHUB_ACTOR"]
                    password = env["GITHUB_TOKEN"]
                }
            }
        }
    }
}

minecraft {
    version(rootProp["minecraft"])
}

dependencies {
    compileOnly("lol.bai:badpackets:mojmap-${rootProp["badpackets"]}")
    compileOnly("org.spongepowered:mixin:0.8.5")

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
    val pluginVanilla by creating
    val pluginTest by creating

    listOf(api, buildConst, mixin, pluginCore, pluginVanilla, pluginTest).applyEach {
        compileClasspath += main.compileClasspath
    }
    listOf(main, pluginCore, pluginVanilla, pluginTest).applyEach {
        compileClasspath += api.output + mixin.output + buildConst.output
    }
    main.apply {
        compileClasspath += minecraftless.output
    }
    buildConst.apply {
        compiledBy("generateTranslationClass")
    }
}

dependencies {
    val minecraftlessCompileOnly by configurations

    minecraftlessCompileOnly("com.google.code.gson:gson:2.8.9")
    dokkaPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.20")
}

task<GenerateTranslationTask>("generateTranslationClass") {
    group = "translation"

    input.set(file("src/resources/resources/assets/waila/lang/en_us.json"))
    output.set(file("src/buildConst/java"))
    className.set("mcp.mobius.waila.buildconst.Tl")
    skipPaths.set(setOf("waila"))
}

tasks.named("compileBuildConstJava") {
    dependsOn("generateTranslationClass")
}

task<FormatTranslationTask>("formatTranslation") {
    group = "translation"

    translationDir.set(file("src/resources/resources/assets/waila/lang"))
}

task<FormatTranslationTask>("validateTranslation") {
    group = "translation"

    translationDir.set(file("src/resources/resources/assets/waila/lang"))
    test.set(true)
}

task<Javadoc>("apiJavadoc") {
    group = "documentation"

    val api by sourceSets
    source = api.allJava
    classpath = api.compileClasspath
    setDestinationDir(file("docs/javadocs"))
}

task<DokkaTask>("apiDokka") {
    moduleVersion.set("${prop["majorVersion"]}.x")
    outputDirectory.set(file("docs/dokka"))
    suppressInheritedMembers.set(true)

    dokkaSourceSets {
        create("api") {
            val api by sourceSets
            sourceRoots.from(api.allJava)
            classpath.from(api.compileClasspath)

            sourceLink {
                localDirectory.set(file("src"))
                remoteUrl.set(URL("https://github.com/badasintended/wthit/tree/dev/master/src"))
            }
        }
    }
}
