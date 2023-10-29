pluginManagement {
	includeBuild("build-logic")
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
		mavenCentral()
		maven { url = uri("https://jitpack.io") }
	}
}

rootProject.name = "Approach"
include(":app")
include(":core:analytics")
include(":core:common")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:designsystem")
include(":core:featureflags")
include(":core:model")
include(":feature:alleyslist")
include(":feature:alleyslist:ui")
include(":feature:analytics")
include(":feature:analytics:ui")
include(":feature:gameseditor")
include(":feature:gameseditor:ui")
include(":feature:statisticswidget")