plugins {
    id("fabric-loom").version("0.8-SNAPSHOT")
}

loom {
    accessWidener = file("src/main/resources/wthit.accesswidener")
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProp["minecraft"]}")
    mappings(loom.officialMojangMappings())
}
