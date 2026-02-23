plugins {
    id("de.undercouch.download")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

val badpacketsApi: Configuration by configurations.creating {
    isTransitive = false
}

dependencies {
    paperweight.paperDevBundle("${rootProp["minecraft"]}-R0.1-SNAPSHOT")
    compileOnly("lol.bai:badpackets:mojmap-${rootProp["badpackets"]}")
    badpacketsApi("lol.bai:badpackets:mojmap-${rootProp["badpackets"]}")
}

sourceSets {
    val main by getting
    val rootSourceSets = rootProject.extensions.getByType<SourceSetContainer>()

    main.apply {
        resources.srcDir(rootProject.file("src/resources/resources"))
        rootSourceSets.forEach {
            compileClasspath += it.output
            runtimeClasspath += it.output
        }
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("paper-plugin.yml") {
            expand("version" to project.version)
        }
    }

    val jar by getting(Jar::class) {
        rootProject.extensions.getByType<SourceSetContainer>()
            .filter { it.name != "mixin" && it.name != "buildConst" && it.name != "test" }
            .forEach { from(it.output) }

        // Bundle BadPackets API classes — needed at runtime because Paper doesn't have BadPackets as a mod,
        // but root project classes (DataWriter, Packets, etc.) reference BadPackets types in method signatures.
        from(badpacketsApi.map { if (it.isDirectory) it else zipTree(it) }) {
            include("lol/bai/badpackets/api/**")
        }
    }
}
