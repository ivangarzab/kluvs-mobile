import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import utils.getPropertyOrEnvVar

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlinx.kover")
    id("com.codingfeline.buildkonfig") version "+"
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.sentry)
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

            export(project(":core:model"))
            export(project(":core:auth"))
            export(project(":core:data"))
            export(project(":core:presentation"))
            export(project(":feature:auth"))
            export(project(":feature:clubs"))
            export(project(":feature:member"))
            export(libs.bark)
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            api(project(":core:model"))
            api(project(":core:auth"))
            api(project(":core:data"))
            api(project(":core:presentation"))
            implementation(project(":core:network"))

            api(project(":feature:auth"))
            api(project(":feature:clubs"))
            api(project(":feature:member"))

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization)
            api(libs.androidx.lifecycle.viewmodel)

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

buildkonfig {
    packageName = "com.ivangarzab.kluvs.shared"
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        val sentryDns = getPropertyOrEnvVar("SENTRY_DNS")
        require(sentryDns.isNotEmpty()) {
            "Make sure to provide the SENTRY_DNS in your global gradle.properties file."
        }
        buildConfigField(
            Type.STRING,
            "SENTRY_DNS",
            sentryDns
        )
        buildConfigField(Type.BOOLEAN, "DEBUG", "false")
    }
    defaultConfigs("debug") {
        buildConfigField(Type.BOOLEAN, "DEBUG", "true")
    }

}

sentryKmp {
    autoInstall {
        enabled = true // Automatically adds the KMP dependency to commonMain
        linker { // Bridge the gap into the iOS env
            enabled = true
            xcodeprojPath = "iosApp/Kluvs.xcodeproj"
        }
    }
}
