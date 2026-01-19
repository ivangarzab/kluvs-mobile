plugins {
    id("kluvs.kmp.library")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {

        }
        androidMain.dependencies {


        }
        iosMain.dependencies {

        }
    }
}