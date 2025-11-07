pluginManagement {
    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OnTrack"
include(":app")
include(":core:network")
include(":core:di")
include(":core:compose")
include(":feature:home")
include(":feature:stationsearch")
include(":core:domain")
include(":core:data")
include(":core:database")
include(":core:navigation")
include(":feature:servicelist")
include(":core:platform")
include(":feature:servicedetail")
include(":wear")
include(":baselineprofile")
include(":core:datastore")
