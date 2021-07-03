platform(Platform.FABRIC)

repositories {
    maven("https://bai.jfrog.io/artifactory/maven")
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProp["fabricLoader"]}")

    modImplementation("dev.inkwell:hermes:1.1.0+1.17")

    modOptional("net.fabricmc.fabric-api:fabric-api:${rootProp["fabricApi"]}")
    modOptional("com.terraformersmc:modmenu:${rootProp["modMenu"]}")
    modOptional("me.shedaniel:RoughlyEnoughItems-fabric:${rootProp["rei"]}")
}

publishToMaven()
publishToCurseforge()
publishToModrinth()
