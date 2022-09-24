import org.gradle.api.Project

private val propDelegate = hashMapOf<Project, PropertyAccessor>()
val Project.prop
    get() = propDelegate.getOrPut(this) { PropertyAccessor(this) }

val Project.rootProp
    get() = rootProject.prop

class PropertyAccessor internal constructor(
    private val project: Project
) {

    fun has(key: String): Boolean {
        return project.hasProperty(key)
    }

    inline fun ifPresent(key: String, func: (String) -> Unit) {
        if (has(key)) func(get(key))
    }

    operator fun get(key: String): String {
        return project.property(key) as String
    }

}
