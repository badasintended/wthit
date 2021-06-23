import java.nio.charset.StandardCharsets

plugins {
    base
    id("architectury-plugin")
    id("dev.architectury.loom").apply(false)
}

version = env["MOD_VERSION"] ?: "local"

architectury {
    minecraft = rootProp["minecraft"]
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    base {
        archivesBaseName = "${rootProject.base.archivesBaseName}-${project.name}"
    }

    dependencies {
        "minecraft"("com.mojang:minecraft:${rootProp["minecraft"]}")
        "mappings"("net.fabricmc:yarn:${rootProp["yarn"]}:v2")
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")

    version = rootProject.version

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_16
        targetCompatibility = JavaVersion.VERSION_16
    }

    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.name()
        options.release.set(16)
    }

    configure<JavaPluginExtension> {
        withSourcesJar()
    }
}
