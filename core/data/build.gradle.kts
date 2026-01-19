plugins {
    id("kluvs.kmp.library")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:model"))
            implementation(project(":core:network"))
            implementation(project(":core:auth"))

            implementation(libs.ktor.client.core)
            implementation(libs.supabase.functions)

            implementation(libs.koin)
            implementation(libs.bark)
        }
        commonTest.dependencies {

        }
        androidMain.dependencies {


        }
        iosMain.dependencies {

        }
    }
}