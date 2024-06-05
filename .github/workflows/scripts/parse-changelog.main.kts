#!/usr/bin/env kotlin

@file:DependsOn("com.google.code.gson:gson:2.9.0")

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class Environment {
    val githubOutput = File(System.getenv("GITHUB_OUTPUT"))

    val branch = System.getenv("BRANCH")!!
    val changelogs = Gson().fromJson<Map<String, Changelog>>(System.getenv("CHANGELOGS"), object : TypeToken<Map<String, Changelog>>() {}.type)!!
}

class Changelog(
    val version: String,
    val changelog: String,
    val java: String,
    val minecraft: String
)

val env = Environment()
val changelog = env.changelogs[env.branch]!!

val output = mapOf(
    "version" to changelog.version,
    "java" to changelog.java,
    "minecraft" to changelog.minecraft,
    "prerelease" to (env.branch == "dev/snapshot"),
    "changelog" to Gson().toJson(
        "${changelog.changelog}\n_Full changelog can be seen at <https://raw.githubusercontent.com/badasintended/wthit/dev/master/CHANGELOG.txt>_"
    )
)

output.forEach { (key, value) ->
    env.githubOutput.appendText("${key}=${value}\n")
}
