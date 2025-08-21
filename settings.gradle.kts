// In settings.gradle.kts at the root of your project

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral() // For Decompose, Ktor, Koin, etc.
        maven("https://packages.jetbrains.team/maven/p/grazi/grazie-platform-public/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "EchoVerse"

// IMPORTANT: Ensure the old ":shared" module is NOT included
include(":androidApp")
include(":iosApp")
include(":core:ai")
include(":core:data")
include(":core:domain")
include(":core:ui")
include(":feature:home")