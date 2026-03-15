plugins {
    id("net.fabricmc.fabric-loom")
}

setupPlatform()

dependencies {
    minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
    implementation("net.fabricmc:fabric-loader:${rootProp["fabricLoader"]}")

    compileOnly("net.fabricmc.fabric-api:fabric-api:${rootProp["fabricApi"]}")
    compileOnly("com.terraformersmc:modmenu:${rootProp["modMenu"]}")
    compileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${rootProp["rei"]}")
    compileOnly("dev.emi:emi-fabric:${rootProp["emi"]}")
    compileOnly("teamreborn:energy:${rootProp["trEnergy"]}")
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

tasks.generateDLIConfig {
    enabled = false
}
