// In core/ai/build.gradle.kts

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.jetpackages.echoverse.core.ai"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        getByName("commonMain").dependencies {
            api(project(":core:domain"))

            // Serialization Dependencies
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)

            // Koog Dependencies (UNCHANGED as requested)
            implementation(libs.prompt.structure)
            implementation(libs.agents.core)
            implementation(libs.agents.tools)
            implementation(libs.agents.utils)
            implementation(libs.agents.features.common)
            implementation(libs.agents.features.tokenizer)
            implementation(libs.koog.prompt)
            implementation(libs.koog.agents)
            implementation(libs.koog.agents.jvm)
            implementation(libs.koog.google.client.jvm)

            implementation(libs.koin.core)
        }
    }
}