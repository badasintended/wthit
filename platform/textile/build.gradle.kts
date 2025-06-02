plugins {
    id("fabric-loom")
}

setupPlatform()

dependencies {
    minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${rootProp["fabricLoader"]}")

    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${rootProp["fabricApi"]}")
    modCompileOnly("com.terraformersmc:modmenu:${rootProp["modMenu"]}")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${rootProp["rei"]}")
    modCompileOnly("dev.emi:emi-fabric:${rootProp["emi"]}")
    modCompileOnly("teamreborn:energy:${rootProp["trEnergy"]}")
}

sourceSets {
    main {
        resources.setSrcDirs(emptyList<Any>())
    }
}

loom {
    interfaceInjection.enableDependencyInterfaceInjection.set(false)
    runs {
        configureEach {
            isIdeConfigGenerated = false
        }
    }
}

tasks.compileJava {
    exclude("mcp/mobius/waila/mixed/**")
    exclude("mcp/mobius/waila/mixin/**")
}

tasks.remapJar {
    enabled = false
}

tasks.remapSourcesJar {
    enabled = false
}

tasks.generateRemapClasspath {
    enabled = false
}

tasks.generateDLIConfig {
    enabled = false
}
