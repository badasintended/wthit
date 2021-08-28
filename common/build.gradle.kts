plugins {
    id("fabric-loom").version("0.8-SNAPSHOT")
}

loom {
    accessWidener = project(":platform:fabric").file("src/main/resources/wthit.accesswidener")
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

