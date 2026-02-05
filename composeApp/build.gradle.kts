import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseAppDistribution)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.splashscreen)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.test.junit)
            implementation(libs.androidx.test.runner)
        }
        commonMain.dependencies {
            implementation(projects.shared)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.koin)
            implementation(libs.koin.compose)
            implementation(libs.bark)
            implementation(libs.coil.compose)
            implementation(libs.coil.network)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.ivangarzab.kluvs"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.ivangarzab.kluvs"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            firebaseAppDistribution {
                serviceCredentialsFile = System.getenv("FIREBASE_CREDENTIALS_FILE")
                    ?: "${System.getProperty("user.home")}/.config/firebase/kluvs-app-distribution.json"
                artifactType = "APK"
                groups = "og"
            }
        }

        getByName("debug") {
            firebaseAppDistribution {
                serviceCredentialsFile = System.getenv("FIREBASE_CREDENTIALS_FILE")
                    ?: "${System.getProperty("user.home")}/.config/firebase/kluvs-app-distribution.json"
                artifactType = "APK"
                groups = "og"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

