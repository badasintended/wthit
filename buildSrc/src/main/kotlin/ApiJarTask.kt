import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType

abstract class ApiJarTask : Jar() {

    fun <T : Jar> fullJar(fullJar: TaskProvider<T>) {
        dependsOn(fullJar)
        project.tasks["build"].dependsOn(this)

        val classifier = fullJar.get().archiveClassifier.orNull
        if (classifier.isNullOrEmpty()) {
            archiveClassifier.set("api")
        } else {
            archiveClassifier.set("api-${classifier}")
        }

        val stub = project.extensions.getByType<SourceSetContainer>().findByName("stub")
        if (stub != null) {
            from(stub.output)
        }

        from(project.zipTree(fullJar.get().archiveFile)) {
            include("mcp/mobius/waila/api/**")
        }
    }

}
