plugins {
    id("kluvs.kmp.library")
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:model"))
            api(libs.androidx.lifecycle.viewmodel)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            implementation(libs.koin)
            implementation(libs.bark)
        }
    }
}