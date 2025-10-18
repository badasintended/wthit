#!/usr/bin/env kotlin

@file:DependsOn("org.kohsuke:github-api:1.314")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")

@file:Suppress("ConstPropertyName", "MayBeConstant")

import eu.jrie.jetbrains.kotlinshell.shell.shell
import org.kohsuke.github.GitHubBuilder
import java.io.File
import java.nio.file.Files

// -------
// Globals
// -------

object Env {
    val GITHUB_TOKEN = System.getenv("GITHUB_TOKEN")!!
    val GITHUB_REPOSITORY = System.getenv("GITHUB_REPOSITORY")!!
    val CURSEFORGE_API = System.getenv("CURSEFORGE_API")!!
    val MODRINTH_TOKEN = System.getenv("MODRINTH_TOKEN")!!
    val MAVEN_USERNAME = System.getenv("MAVEN_USERNAME")!!
    val MAVEN_PASSWORD = System.getenv("MAVEN_PASSWORD")!!
}

object Github {
    val github = GitHubBuilder().withOAuthToken(Env.GITHUB_TOKEN).build()!!
    val repo = github.getRepository(Env.GITHUB_REPOSITORY)!!
}

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int
) : Comparable<Version> {

    constructor(str: String) : this(str.split(".", limit = 3))
    constructor(str: List<String>) : this(str[0].toInt(), str[1].toInt(), str[2].toInt())

    private fun toInt(): Int {
        return patch + (minor * 1000) + (major * 1000000)
    }

    override fun compareTo(other: Version): Int {
        return toInt().compareTo(other.toInt())
    }

    override fun toString(): String {
        return "${major}.${minor}.${patch}"
    }

}

class Release(
    val branch: String,
    val version: String,
    val java: String,
    val minecraft: String,
    val changelog: String
)

// -------------------------------------------------------------
// Stage 1: Read changelog to determine what branches to release
// -------------------------------------------------------------

object S1 {
    val output = mutableMapOf<Int, Release>()
    val changelog = "CHANGELOG.txt"

    object Regexes {
        val header = Regex("""^(\d+)[.]x\s+-\s+(.+)\s-\s(.+)\s-\sjava\s(.+)$""")
        val version = Regex("""^^(\d+[.]\d+[.]\d+)\s+(-\s.+)$""")
        val desc = Regex("""^\s{9}(\s*-\s.+)$""")
    }
}

run {
    println("Reading ${S1.changelog}")

    var currentMajor = -1
    var currentVersion = Version(-1, -1, -1)
    var currentChangelog = StringBuilder()
    var currentMinecraft = ""
    var currentBranch = ""
    var currentJava = ""

    var foundLatest = false
    var skipHeader = false

    File(S1.changelog).forEachLine l@{ line ->
        if (line.trim().isEmpty()) return@l

        val headerMatch = S1.Regexes.header.matchEntire(line)
        if (headerMatch != null) {
            if (currentMajor != -1 && !skipHeader) {
                val changelog = Release(
                    currentBranch,
                    currentVersion.toString(),
                    currentJava,
                    currentMinecraft,
                    currentChangelog.toString()
                )

                val release = Github.repo.getReleaseByTagName(changelog.version)
                if (release == null) {
                    S1.output[currentMajor] = changelog
                }
            }

            currentMajor = headerMatch.groups[1]!!.value.toInt()

            if (currentMajor == 1) {
                skipHeader = true
            } else {
                skipHeader = false

                currentChangelog = StringBuilder()
                currentMinecraft = headerMatch.groups[2]!!.value
                currentBranch = headerMatch.groups[3]!!.value
                currentJava = headerMatch.groups[4]!!.value
            }

            foundLatest = false
            return@l
        }

        if (skipHeader) return@l
        if (foundLatest) return@l

        val versionMatch = S1.Regexes.version.matchEntire(line)
        if (versionMatch != null) {
            if (currentVersion.major == currentMajor) {
                foundLatest = true
                return@l
            }

            currentVersion = Version(versionMatch.groups[1]!!.value)
            currentChangelog.appendLine(versionMatch.groups[2]!!.value)
            return@l
        }

        val descMatch = S1.Regexes.desc.matchEntire(line)
        if (descMatch != null) {
            currentChangelog.appendLine(descMatch.groups[1]!!.value)
            return@l
        }
    }
}

// -----------------------------------------
// Stage 2: Release each branch sequentially
// -----------------------------------------

shell {
    suspend fun exec(str: String) {
        println("EXEC: $str")
        str.invoke()
        println()
    }

    val root = File("build/__release")
    root.mkdirs()

    S1.output.toSortedMap { k, _ -> k }.reversed().values.forEach { release ->
        println("Releasing ${release.version} from branch ${release.branch}")

        val workDir = root.resolve(release.branch)
        val clone = workDir.mkdirs()

        cd(workDir)
        if (clone || workDir.list()!!.isEmpty()) {
            exec("git clone https://github.com/${Env.GITHUB_REPOSITORY}.git --branch ${release.branch} .")
        } else {
            exec("git fetch origin")
            exec("git pull")
        }

        export("MOD_VERSION" to release.version)
        export("CHANGELOG" to release.changelog)
        export("CURSEFORGE_API" to Env.CURSEFORGE_API)
        export("MODRINTH_TOKEN" to Env.MODRINTH_TOKEN)
        export("MAVEN_USERNAME" to Env.MAVEN_USERNAME)
        export("MAVEN_PASSWORD" to Env.MAVEN_PASSWORD)
        export("JAVA_HOME" to System.getenv("JAVA_${release.java}_HOME")!!)

        exec("./gradlew clean build publish publishMods")
        exec("./gradlew --stop")

        val releaseName = "[${release.minecraft}] ${release.version}"
        println("Creating release $releaseName")
        val ghRelease = Github.repo.createRelease(release.version)
            .name(releaseName)
            .commitish(release.branch)
            .body(release.changelog)
            .create()

        fun upload(lib: File) {
            if (lib.nameWithoutExtension.endsWith("-sources")) return

            println("Uploading ${lib.toRelativeString(workDir)}")
            val contentType = Files.probeContentType(lib.toPath())
            println("Content-type $contentType")
            ghRelease.uploadAsset(lib, contentType)
        }

        workDir.resolve("platform").listFiles()?.forEach { platform ->
            platform.resolve("build/libs").listFiles()?.forEach(::upload)
        }

        workDir.resolve("fabric/build/libs").listFiles()?.forEach(::upload)
        workDir.resolve("forge/build/libs").listFiles()?.forEach(::upload)

        println("\n")
    }
}
