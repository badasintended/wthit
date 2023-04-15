#!/usr/bin/env kotlin

@file:DependsOn("com.google.code.gson:gson:2.9.0")
@file:DependsOn("org.kohsuke:github-api:1.314")

import com.google.gson.Gson
import org.kohsuke.github.GitHubBuilder
import java.io.File
import kotlin.reflect.full.memberProperties

val script = this

class Environment {
    val github = Github()

    class Github {
        val workspace = File(System.getenv("GITHUB_WORKSPACE"))
        val output = File(System.getenv("GITHUB_OUTPUT"))
        val token = System.getenv("GITHUB_TOKEN")!!
        val repo = System.getenv("GITHUB_REPOSITORY")!!
    }
}

class Regexes {
    val header = Regex("""^(\d+)[.]x\s+-\s+(.+)\s-\s(.+)\s-\sjava\s(.+)$""")
    val version = Regex("""^^(\d+[.]\d+[.]\d+)\s+(-\s.+)$""")
    val desc = Regex("""^\s{9}(\s*-\s.+)$""")
}

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int
) : Comparable<Version> {

    constructor(str: String) : this(str.split(".", limit = 3))
    private constructor(str: List<String>) : this(str[0].toInt(), str[1].toInt(), str[2].toInt())

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

class Changelog(
    val version: String,
    val java: String,
    val minecraft: String,
    val changelog: String
)

val env = Environment()
val regexes = Regexes()
val github = GitHubBuilder().withOAuthToken(env.github.token).build()!!
val repo = github.getRepository(env.github.repo)!!

val branches = mutableMapOf<Int, String>()
val changelogs = mutableMapOf<String, Changelog>()

var currentMajor = -1
var currentVersion = Version(-1, -1, -1)
var currentChangelog = StringBuilder()
var currentMinecraft = ""
var currentBranch = ""
var currentJava = ""

var foundLatest = false
var skipHeader = false

env.github.workspace.resolve("CHANGELOG.txt").reader().forEachLine l@{ line ->
    if (line.isBlank()) return@l

    val headerMatch = regexes.header.matchEntire(line)
    if (headerMatch != null) {
        if (currentMajor != -1 && !skipHeader) {
            val changelog = Changelog(currentVersion.toString(), currentJava, currentMinecraft, currentChangelog.toString())
            val release = repo.getReleaseByTagName(changelog.version)

            if (release == null) {
                branches[currentMajor] = currentBranch
                changelogs[currentBranch] = changelog
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

    val versionMatch = regexes.version.matchEntire(line)
    if (versionMatch != null) {
        if (currentVersion.major == currentMajor) {
            foundLatest = true
            return@l
        }

        currentVersion = Version(versionMatch.groups[1]!!.value)
        currentChangelog.appendLine(versionMatch.groups[2]!!.value)
        return@l
    }


    val descMatch = regexes.desc.matchEntire(line)
    if (descMatch != null) {
        currentChangelog.appendLine(descMatch.groups[1]!!.value)
        return@l
    }
}

class Output {
    val empty = script.branches.isEmpty().toString()
    val branches = script.branches.keys.sorted().joinToString(separator = "\", \"", prefix = "[\"", postfix = "\"]") { script.branches[it]!! }
    val changelogs = Gson().toJson(script.changelogs)!!
}

val output = Output()

Output::class.memberProperties.forEach { property ->
    env.github.output.appendText("${property.name}=${property.get(output)}\n")
}
