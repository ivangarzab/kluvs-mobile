plugins {
    // Plugins provided by buildSrc are available project-wide via convention plugins.
    // Only declare plugins here that are NOT in buildSrc dependencies.
    alias(libs.plugins.kotlinSerialization) apply false
}