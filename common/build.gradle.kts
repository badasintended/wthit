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
