plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.mokkery)
    id("com.codingfeline.buildkonfig") version "+"
    id("org.jetbrains.kotlinx.kover") version "0.9.3"
}

/**
 * Attempt to get a Gradle Property called [name]; if it fails, attempt to get it as an
 * Environment Variable; if that fails, return [default].
 */
fun getPropertyOrEnvVar(name: String, default: String = ""): String =
    (findProperty(name) as String?)
        ?: System.getenv(name)
        ?: default

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
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
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization)
            api(libs.androidx.lifecycle.viewmodel)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            implementation(libs.supabase)
            implementation(libs.supabase.functions)
            implementation(libs.supabase.auth)

            implementation(libs.koin)
            implementation(libs.bark)

        }
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.koin.android)
            implementation(libs.koin.compose)
            implementation(libs.androidx.security.crypto)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
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
    namespace = "com.ivangarzab.bookclub.shared"
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

buildkonfig {
    packageName = "com.ivangarzab.bookclub.shared"

    defaultConfigs {
        // Production Supabase credentials
        val supabaseUrl: String = getPropertyOrEnvVar("SUPABASE_URL")
        val supabaseKey: String = getPropertyOrEnvVar("SUPABASE_KEY")
        require(supabaseUrl.isNotEmpty() && supabaseKey.isNotEmpty()) {
            "Make sure to provide the SUPABASE_URL and SUPABASE_KEY in your global gradle.properties file."
        }
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SUPABASE_KEY",
            supabaseKey
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SUPABASE_URL",
            supabaseUrl
        )

        // Testing Supabase credentials
        val testSupabaseUrl: String = getPropertyOrEnvVar("TEST_SUPABASE_URL")
        val testSupabaseKey: String = getPropertyOrEnvVar("TEST_SUPABASE_KEY")
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "TEST_SUPABASE_KEY",
            testSupabaseKey
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "TEST_SUPABASE_URL",
            testSupabaseUrl
        )
    }
}

kover {
    reports {
        filters {
            excludes {
                classes("*.BuildConfig", "*.BuildKonfig") // generated code
                packages("com.ivangarzab.bookclub.data.remote.dtos") // Dtos
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
