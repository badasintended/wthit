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
    val api by creating

    main {
        compileClasspath += api.output
        runtimeClasspath += api.output
    }
}

configurations {
    val compileClasspath by getting
    get("apiImplementation").extendsFrom(compileClasspath)
}

tasks.remapJar {
    enabled = false
}

tasks.remapSourcesJar {
    enabled = false
}

