plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlinx.kover") version "0.9.3"
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.mokkery)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            api(project(":core:model"))
            implementation(project(":core:network"))
            implementation(project(":core:auth"))

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization)
            api(libs.androidx.lifecycle.viewmodel)

            implementation(libs.supabase)
            implementation(libs.supabase.functions)

            implementation(libs.koin)
            implementation(libs.bark)

        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.koin.compose)
        }
        iosMain.dependencies {

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.koin.test)
        }
    }
}

android {
    namespace = "com.ivangarzab.kluvs.shared"
    //noinspection GradleDependency
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kover {
    reports {
        filters {
            excludes {
                classes("*.BuildConfig", "*.BuildKonfig") // generated code
                packages("com.ivangarzab.kluvs.data.remote.dtos") // Dtos
                packages("**.di") // Dependency Injection
                // Add other exclusions as needed
            }
        }
    }
}

afterEvaluate {
    /**
     * The purpose of this task extension is to allow the :shared:testDebugUnitTest to accept
     * manual exclusions, and make it easier to run the Unit Test suit without running the
     * Integration tests classes contained within it.
     */
    tasks.named("testDebugUnitTest", Test::class) {
        if (project.hasProperty("excludeTests")) {
            val exclusions = project.property("excludeTests").toString().split(",")
            exclude(exclusions)
        }
    }
}
