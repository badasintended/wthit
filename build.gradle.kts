import java.nio.charset.StandardCharsets

plugins {
    id("fabric-loom").version("0.10.+")
}

version = env["MOD_VERSION"] ?: "${prop["majorVersion"]}.999-${env["GIT_HASH"] ?: "local"}"

allprojects {
    apply(plugin = "base")
    apply(plugin = "java")

    version = rootProject.version

    java {
        sourceCompatibility = JavaVersion.VERSION_16
        targetCompatibility = JavaVersion.VERSION_16

        withSourcesJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.name()
        options.release.set(16)
    }
}

subprojects {
    base {
        archivesName.set("${rootProject.base.archivesName.get()}-${project.name}")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${rootProp["fabricLoader"]}")
}

loom {
    accessWidenerPath.set(project(":fabric").file("src/main/resources/wthit.accesswidener"))
}

sourceSets {
    val main by getting
    val api by creating
    val impl by creating
    val util by creating
    val pluginCore by creating
    val pluginVanilla by creating
    val pluginTest by creating

    listOf(api, impl, util, pluginCore, pluginVanilla, pluginTest).applyEach {
        compileClasspath += main.compileClasspath
    }
    listOf(api, main).applyEach {
        compileClasspath += impl.output
    }
    listOf(main, util, pluginCore, pluginVanilla, pluginTest).applyEach {
        compileClasspath += api.output
    }
    listOf(main, pluginCore, pluginVanilla, pluginTest).applyEach {
        compileClasspath += util.output
    }
}

tasks.remapJar {
    enabled = false
}

tasks.remapSourcesJar {
    enabled = false
}
