import java.nio.charset.StandardCharsets

plugins {
    base
    java
}

version = env["MOD_VERSION"] ?: "999-local"

allprojects {
    apply(plugin = "base")
    apply(plugin = "java")

    version = rootProject.version

    java {
        sourceCompatibility = JavaVersion.VERSION_16
        targetCompatibility = JavaVersion.VERSION_16

        withSourcesJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.name()
        options.release.set(16)
    }
}

subprojects {
    base {
        archivesName.set("${rootProject.base.archivesName.get()}-${project.name}")
    }
}
