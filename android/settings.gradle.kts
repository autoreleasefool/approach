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
include(":core:database")
include(":core:datastore")
include(":core:featureflags")
include(":core:model")
