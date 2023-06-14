import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.named

fun Project.setupPlatform() {
    val rootSourceSets = rootProject.extensions.getByType<SourceSetContainer>()
    val sourceSets = extensions.getByType<SourceSetContainer>()

    sourceSets.apply {
        val main by getting
        val plugin by creating

        plugin.apply {
            compileClasspath += main.compileClasspath + rootSourceSets["api"].output
        }

        main.apply {
            resources.srcDir(rootProject.file("src/resources/resources"))
            rootSourceSets.forEach {
                compileClasspath += it.output
                runtimeClasspath += it.output
            }
            runtimeClasspath += plugin.output
        }
    }

    // mixin ap need to access the classes so it needs to be compiled on each platform
    tasks.named<JavaCompile>("compileJava") {
        source(rootSourceSets["mixin"].allJava)
    }

    tasks.named<Jar>("jar") {
        from(sourceSets["plugin"].output)
        rootSourceSets.filterNot { it.name == "mixin" || it.name == "buildConst" }.forEach {
            from(it.output)
        }
    }

    tasks.named<Jar>("sourcesJar") {
        dependsOn(":generateTranslationClass")
        from(sourceSets["plugin"].allSource)
        rootSourceSets.forEach {
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
