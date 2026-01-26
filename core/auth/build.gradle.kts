plugins {
    id("kluvs.kmp.library")
    alias(libs.plugins.mokkery)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:model"))
            implementation(project(":core:network"))
            implementation(project(":core:database"))

            implementation(libs.supabase.auth)
            implementation(libs.room.runtime)

            implementation(libs.bark)
            implementation(libs.koin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.security.crypto)
            implementation(libs.androidx.core)
        }
        iosMain.dependencies {

        }
    }
}