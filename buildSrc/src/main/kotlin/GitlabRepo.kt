import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.authentication.http.HttpHeaderAuthentication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.credentials
import java.net.URI

fun RepositoryHandler.gitlabMaven() {
    maven {
        url = URI("https://gitlab.com/api/v4/projects/25106863/packages/maven")
        name = "GitLab"
        credentials(HttpHeaderCredentials::class) {
            name = "Private-Token"
            value = env["GITLAB_TOKEN"]
        }
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
}
