plugins {
    // Plugins provided by buildSrc are available project-wide via convention plugins.
    // Only declare plugins here that are NOT in buildSrc dependencies.
    alias(libs.plugins.kotlinSerialization) apply false
    id("org.jetbrains.kotlinx.kover")
}

// Configure aggregation
kover {
    reports {
        // This ensures the root report includes all subprojects (core, feature, shared)
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
