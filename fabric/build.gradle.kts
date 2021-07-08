platform {
    fabric
}

repositories {
    maven("https://bai.jfrog.io/artifactory/maven")
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProp["fabricLoader"]}")

    modImplementation("dev.inkwell:hermes:1.1.0+1.17")

    modCompileRuntime("net.fabricmc.fabric-api:fabric-api:${rootProp["fabricApi"]}")
    modCompileRuntime("com.terraformersmc:modmenu:${rootProp["modMenu"]}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${rootProp["rei"]}")
    modRuntime("me.shedaniel:RoughlyEnoughItems-fabric:${rootProp["rei"]}")
}

publish {
    apiJar
    maven
    curseforge
    modrinth
}
