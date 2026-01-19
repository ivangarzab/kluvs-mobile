plugins {
    id("kluvs.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:model"))
            api(project(":core:presentation"))
            implementation(project(":core:auth"))

            api(libs.androidx.lifecycle.viewmodel)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.koin)
            implementation(libs.bark)
        }

        iosMain.dependencies {
            // Inherited from convention plugin
        }
    }
}