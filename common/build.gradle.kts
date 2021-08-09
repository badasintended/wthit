plugins {
    id("fabric-loom").version("0.8-SNAPSHOT")
}

loom {
    accessWidener = project(":fabric").file("src/main/resources/wthit.accesswidener")
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${rootProp["fabricLoader"]}")
}

sourceSets {
    val main by getting
    val api by creating
    val impl by creating
    val util by creating
    val pluginCore by creating
    val pluginVanilla by creating

    listOf(api, impl, util, pluginCore, pluginVanilla).applyEach {
        compileClasspath += main.compileClasspath
    }
    listOf(api, main).applyEach {
        compileClasspath += impl.output
    }
    listOf(main, util, pluginCore, pluginVanilla).applyEach {
        compileClasspath += api.output
    }
    listOf(main, pluginCore, pluginVanilla).applyEach {
        compileClasspath += util.output
    }
}

tasks.remapJar {
    enabled = false
}

tasks.remapSourcesJar {
    enabled = false
}

