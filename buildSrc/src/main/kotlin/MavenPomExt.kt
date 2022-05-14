import groovy.util.Node
import org.gradle.api.publish.maven.MavenPom

fun MavenPom.withDependencies(action: MavenDependency.() -> Unit) {
    withXml {
        asNode().appendNode("dependencies").apply {
            action(MavenDependency(this))
        }
    }
}

class MavenDependency(val node: Node) {
    fun runtime(notation: String) {
        "runtime"(notation)
    }

    fun compile(notation: String) {
        "compile"(notation)
    }

    operator fun String.invoke(notation: String) {
        node.appendNode("dependency").apply {
            val (groupId, artifactId, version) = notation.split(':', limit = 3)
            appendNode("groupId", groupId)
            appendNode("artifactId", artifactId)
            appendNode("version", version)
            appendNode("scope", this@invoke)
        }
    }
}
