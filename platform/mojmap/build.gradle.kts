tasks.jar {
    from(rootProject.sourceSets["api"].output)
    archiveClassifier.set("api")
}

tasks.sourcesJar {
    from(rootProject.sourceSets["api"].allSource)
    archiveClassifier.set("api-sources")
}

afterEvaluate {
    upload {
        mavenApi(tasks.jar.get(), tasks.sourcesJar.get())
    }
}
