import de.undercouch.gradle.tasks.download.Download

plugins {
    id("de.undercouch.download")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:${rootProp["spigotApi"]}")
}

sourceSets {
    val main = getByName("main")
    val minecraftless = rootProject.sourceSets.getByName("minecraftless")

    main.apply {
        compileClasspath += minecraftless.output
        runtimeClasspath += minecraftless.output
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    val jar = getByName<Jar>("jar") {
        from(rootProject.sourceSets["minecraftless"].output)
    }

    val (paperclipMc, paperclipBuild) = prop["paperclip"].split("-", limit = 2)
    val paperclipJar = "paper-${paperclipMc}-${paperclipBuild}.jar"

    val downloadPaperclip = create<Download>("downloadPaperclip") {
        group = "paper"

        src("https://papermc.io/api/v2/projects/paper/versions/${paperclipMc}/builds/${paperclipBuild}/downloads/${paperclipJar}")
        dest(file("run"))
        onlyIfModified(true)
    }

    val copyBuiltJarToPluginFolder = create<Copy>("copyBuiltJarToPluginFolder") {
        group = "paper"
        dependsOn(build)

        from(jar.outputs.files)
        into(file("run/plugins"))
        include(jar.archiveFileName.get())
    }

    val runPaperServer = create<JavaExec>("runPaperServer") {
        group = "paper"
        dependsOn(downloadPaperclip, copyBuiltJarToPluginFolder)

        isIgnoreExitValue = true
        workingDir = file("run")
        classpath = files("run/${paperclipJar}")
        mainClass = "io.papermc.paperclip.Main"
    }
}

afterEvaluate {
    val jar = tasks.jar.get()
    val sourcesJar = tasks.sourcesJar.get()

    upload {
        curseforge(jar)
        maven(jar, sourcesJar)
    }
}
