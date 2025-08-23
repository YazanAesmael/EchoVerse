plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.jetpackages.echoverse.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.jetpackages.echoverse.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    // The ONLY feature dependency needed. It provides the UI.
    implementation(project(":feature:home"))
    implementation(project(":core:ui"))

    // The glue that allows an Android Activity to host Compose content.
    implementation(libs.androidx.activity.compose)

    // The Koin dependency for Android-specific setup.
    implementation(libs.koin.android)

    implementation(libs.napier)
}