tasks.jar {
    rootProject.sourceSets.filterNot { it.name=="buildConst" }.forEach {
        from(it.output)
    }
}

tasks.sourcesJar {
    rootProject.sourceSets.filterNot { it.name=="buildConst" }.forEach {
        from(it.allSource)
    }
}

afterEvaluate {
    val jar = tasks.jar
    val apiJar = tasks.register<ApiJarTask>("apiJar") {
        fullJar(jar)
    }

    val sourcesJar = tasks.sourcesJar
    val apiSourcesJar = tasks.register<ApiJarTask>("apiSourcesJar") {
        fullJar(sourcesJar)
    }

    upload {
        maven(jar, sourcesJar)
        maven(apiJar, apiSourcesJar, suffix = "api")
    }
}
