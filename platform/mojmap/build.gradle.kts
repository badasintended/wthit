tasks.jar {
    rootProject.sourceSets.forEach {
        from(it.output)
    }
}

tasks.sourcesJar {
    rootProject.sourceSets.forEach {
        from(it.allSource)
    }
}

afterEvaluate {
    val jar = tasks.jar.get()
    val apiJar = task<ApiJarTask>("apiJar") {
        fullJar(jar)
    }

    val sourcesJar = tasks.sourcesJar.get()
    val apiSourcesJar = task<ApiJarTask>("apiSourcesJar") {
        fullJar(sourcesJar)
    }

    upload {
        maven(jar, sourcesJar)
        maven(apiJar, apiSourcesJar, suffix = "api")
    }
}
