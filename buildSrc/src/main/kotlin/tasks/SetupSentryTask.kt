package tasks

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.net.URL
import javax.inject.Inject

/**
 * The purpose of this Kotlin task is to download the Sentry.xcframework file in order for
 * iOS to compile correctly in Xcode Cloud.
 */
abstract class SetupSentryTask : DefaultTask() {

    @get:InputFile
    abstract val packageResolvedFile: RegularFileProperty

    @get:OutputDirectory
    abstract val frameworkDestDir: DirectoryProperty

    // INJECTED SERVICES (The fix for Configuration Cache errors)
    @get:Inject
    abstract val fileSystemOperations: FileSystemOperations

    @get:Inject
    abstract val archiveOperations: ArchiveOperations

    @TaskAction
    fun setup() {
        val resolvedFile = packageResolvedFile.get().asFile
        if (!resolvedFile.exists()) {
            throw IllegalStateException("❌ CI Error: Could not find Package.resolved at $resolvedFile")
        }

        println("⚙️ CI: Parsing Sentry version from ${resolvedFile.name}...")

        // 1. Parse JSON to find the version
        val json = JsonSlurper().parse(resolvedFile) as Map<*, *>
        @Suppress("UNCHECKED_CAST")
        val pins = json["pins"] as List<Map<*, *>>
        val sentryPin = pins.find { it["identity"] == "sentry-cocoa" }
            ?: throw IllegalStateException("❌ CI Error: 'sentry-cocoa' not found in Package.resolved")

        val version = (sentryPin["state"] as Map<*, *>)["version"] as String
        println("🎯 CI: Detected locked Sentry version: $version")

        // 2. Download
        val url = "https://github.com/getsentry/sentry-cocoa/releases/download/$version/Sentry.xcframework.zip"
        val zipFile = frameworkDestDir.get().asFile.resolve("Sentry.zip")

        println("⬇️ CI: Downloading $url...")
        URL(url).openStream().use { input ->
            zipFile.outputStream().use { output -> input.copyTo(output) }
        }

        // 3. Unzip using Injected Services (FIXED)
        // We use fileSystemOperations instead of project.copy
        println("📦 CI: Unzipping framework...")
        fileSystemOperations.copy {
            from(archiveOperations.zipTree(zipFile))
            into(frameworkDestDir)
        }

        // 4. Cleanup
        zipFile.delete()
        println("✅ CI: Sentry setup complete.")
    }
}