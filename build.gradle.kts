plugins {
    // Plugins provided by buildSrc are available project-wide via convention plugins.
    // Only declare plugins here that are NOT in buildSrc dependencies.
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.firebaseAppDistribution) apply false
    id("org.jetbrains.kotlinx.kover")
}

// Global test exclusion support - allows excluding tests via -PexcludeTests="**/*IntegrationTest.class"
subprojects {
    tasks.withType<Test>().configureEach {
        if (project.hasProperty("excludeTests")) {
            val exclusions = project.property("excludeTests").toString().split(",")
            exclude(exclusions)
        }
    }
}

// Kover dependencies for aggregation - pulls coverage from all modules
dependencies {
    kover(project(":core:model"))
    kover(project(":core:network"))
    kover(project(":core:auth"))
    kover(project(":core:data"))
    kover(project(":core:presentation"))
    kover(project(":feature:auth"))
    kover(project(":feature:clubs"))
    kover(project(":feature:member"))
    kover(project(":shared"))
}
kover {
    reports {
        total {
            filters {
                excludes {
                    classes("*.BuildConfig", "*.BuildKonfig") // Filter out generated code
                    packages("**.di", "**.dtos") // Filter out DI and DTOs globally
                }
            }
            xml { onCheck = true }
            html { onCheck = true }
        }
    }
}
