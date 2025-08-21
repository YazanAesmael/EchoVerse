// In core/data/build.gradle.kts

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqlDelight)
}

android {
    namespace = "com.jetpackages.echoverse.core.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

sqldelight {
    databases {
        create("EchoVerseDatabase") {
            packageName.set("com.jetpackages.echoverse.db")
        }
    }
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        getByName("commonMain").kotlin.srcDir("build/generated/sqldelight/commonMain")

        val commonMain by getting
        val androidMain by getting

        // Create the intermediate source set for all iOS targets
        val iosMain by creating {
            dependsOn(commonMain)
            // You can add configurations specific to iosMain here if needed
        }

        // Configure individual iOS targets to depend on iosMain
        val iosX64Main by getting {
            dependsOn(iosMain)
        }
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }

        val nativeMain by creating {
            dependsOn(commonMain)
            iosMain.dependsOn(this)
        }

        // Add dependencies
        commonMain.dependencies {
            api(project(":core:domain"))
            implementation(libs.sqlDelight.runtime)
            implementation(libs.sqlDelight.coroutines.extensions)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.datetime)

            implementation(libs.koin.core)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.sqlDelight.android.driver)
        }

        // Add native-specific dependencies to the nativeMain source set
        nativeMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqlDelight.native.driver)
        }
    }
}