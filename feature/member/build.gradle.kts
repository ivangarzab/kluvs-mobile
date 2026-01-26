plugins {
    id("kluvs.kmp.library")
    alias(libs.plugins.mokkery)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))
            implementation(project(":core:presentation"))
            implementation(project(":core:auth"))
            implementation(project(":core:data"))

            api(libs.androidx.lifecycle.viewmodel)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            implementation(libs.koin)
            implementation(libs.bark)
        }
        commonTest.dependencies {
            implementation(project(":core:database"))
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        iosMain.dependencies {
            // Inherited from convention plugin
        }
    }
}