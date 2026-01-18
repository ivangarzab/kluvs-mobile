import config.AndroidConfig
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}

// Common configuration for all :core and :feature KMP modules
kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // Standard iOS targets
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = project.name
            isStatic = true
        }
    }
}

android {
    namespace = "com.ivangarzab.kluvs.${project.name.replace("-", ".")}"
    compileSdk = AndroidConfig.COMPILE_SDK
    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
    }
}
