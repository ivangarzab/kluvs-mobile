plugins {
    id("kluvs.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:model"))
            implementation(project(":core:network"))
            implementation(libs.supabase.auth)

            implementation(libs.bark)
            implementation(libs.koin)
        }
        commonTest.dependencies {

        }
        androidMain.dependencies {
            implementation(libs.androidx.security.crypto)
            implementation(libs.androidx.core)
        }
        iosMain.dependencies {

        }
    }
}