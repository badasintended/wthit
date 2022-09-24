import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.*

fun Project.setupPlatform() {
    val rootSourceSets = rootProject.extensions.getByType<SourceSetContainer>()

    extensions.configure<SourceSetContainer> {
        named("main") {
            resources.srcDir(rootProject.file("src/resources/resources"))
            rootSourceSets.forEach {
                compileClasspath += it.output
                runtimeClasspath += it.output
            }
        }
    }

    // mixin ap need to access the classes so it needs to be compiled on each platform
    tasks.named<JavaCompile>("compileJava") {
        source(rootSourceSets["mixin"].allJava)
    }

    tasks.named<Jar>("jar") {
        rootSourceSets.filterNot { it.name == "mixin" || it.name == "buildConst" }.forEach {
            from(it.output)
        }
    }

    tasks.named<Jar>("sourcesJar") {
        rootSourceSets.filterNot { it.name == "buildConst" }.forEach {
            from(it.allSource)
        }
    }
}

fun Project.setupStub() {
    extensions.configure<SourceSetContainer> {
        val main by getting
        create("stub") {
            compileClasspath += main.compileClasspath
        }
    }
}
