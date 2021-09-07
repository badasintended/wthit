import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.task

fun Project.setupPlatform() {
    val commonJava = buildDir.resolve("commonSrc/java")
    val commonResources = buildDir.resolve("commonSrc/resources")

    extensions.configure<SourceSetContainer> {
        named("main") {
            java.srcDir(commonJava)
            resources.srcDir(commonResources)

            rootProject.extensions.getByType<SourceSetContainer>().forEach {
                compileClasspath += it.output
            }
        }
    }

    task<Copy>("commonSrc") {
        rootProject.extensions.getByType<SourceSetContainer>().forEach {
            copy {
                from(it.allJava)
                into(commonJava)
            }
            copy {
                from(it.resources)
                into(commonResources)
            }
        }
    }

    task<Delete>("deleteCommonSrc") {
        delete(commonJava)
        delete(commonResources)
    }

    listOf(tasks["compileJava"], tasks["processResources"], tasks["sourcesJar"]).applyEach {
        dependsOn("commonSrc")
        finalizedBy("deleteCommonSrc")
    }
}
