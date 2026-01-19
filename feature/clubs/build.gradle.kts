plugins {
    id("kluvs.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:model"))
            implementation(project(":core:data"))
            implementation(project(":core:auth"))

            api(libs.androidx.lifecycle.viewmodel)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.koin)
            implementation(libs.bark)
        }
    }
}