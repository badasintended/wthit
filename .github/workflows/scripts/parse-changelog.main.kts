#!/usr/bin/env kotlin

@file:DependsOn("com.google.code.gson:gson:2.9.0")

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.reflect.full.memberProperties

val script = this

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

class Output {
    val version = script.changelog.version
    val java = script.changelog.java
    val minecraft = script.changelog.minecraft
    val changelog = Gson().toJson(
        "${script.changelog.changelog}\n_Full changelog can be seen at <https://github.com/badasintended/wthit/blob/dev/master/CHANGELOG.txt>_"
    )!!

}

val output = Output()

Output::class.memberProperties.forEach { property ->
    env.githubOutput.appendText("${property.name}=${property.get(output)}\n")
}
