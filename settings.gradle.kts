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

rootProject.name = "PDFBanao"

include(":app")

include(":core:data")
include(":core:image")
include(":core:model")
include(":core:pdf")
include(":core:ui")

include(":feature:create")
include(":feature:library")
include(":feature:settings")
include(":feature:tools")
include(":feature:viewer")
