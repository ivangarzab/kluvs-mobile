plugins {
    id("kluvs.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:model"))
            api(project(":core:presentation"))
            api(project(":core:auth"))
            implementation(project(":core:data"))

            api(libs.androidx.lifecycle.viewmodel)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            implementation(libs.koin)
            implementation(libs.bark)
        }

        iosMain.dependencies {
            // Inherited from convention plugin
        }
    }
}