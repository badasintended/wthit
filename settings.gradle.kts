rootProject.name = "wthit"

fun platform(name: String) {
    include(name)
    project(":${name}").projectDir = file("platform/${name}")
}

platform("bukkit")
platform("fabric")
platform("forge")
platform("mojmap")
