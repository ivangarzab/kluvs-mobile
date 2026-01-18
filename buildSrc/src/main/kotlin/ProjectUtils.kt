import org.gradle.api.Project

/**
 * Attempt to get a Gradle Property called [name]; if it fails, attempt to get it as an
 * Environment Variable; if that fails, return [default].
 */
fun Project.getPropertyOrEnvVar(name: String, default: String = ""): String =
    (findProperty(name) as String?)
        ?: System.getenv(name)
        ?: default