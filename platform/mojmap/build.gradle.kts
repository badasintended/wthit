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
    val apiJar by tasks.registering(ApiJarTask::class) {
        fullJar(jar)
    }

    val sourcesJar = tasks.sourcesJar
    val apiSourcesJar by tasks.registering(ApiJarTask::class) {
        fullJar(sourcesJar)
    }

    upload {
        maven(jar, sourcesJar)
        maven(apiJar, apiSourcesJar, suffix = "api")
    }
}
