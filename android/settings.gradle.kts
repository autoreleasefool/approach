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
include(":core:model:ui")
include(":core:scoresheet")
include(":core:scoring")
include(":core:testing")
include(":feature:accessoriesoverview")
include(":feature:accessoriesoverview:ui")
include(":feature:alleyslist")
include(":feature:alleyslist:ui")
include(":feature:avatarform")
include(":feature:avatarform:ui")
include(":feature:bowlerform")
include(":feature:bowlerform:ui")
include(":feature:bowlerslist:ui")
include(":feature:analytics")
include(":feature:analytics:ui")
include(":feature:gameseditor")
include(":feature:gameseditor:ui")
include(":feature:gearform")
include(":feature:gearform:ui")
include(":feature:gearlist")
include(":feature:gearlist:ui")
include(":feature:resourcepicker")
include(":feature:resourcepicker:ui")
include(":feature:settings")
include(":feature:settings:ui")
include(":feature:statisticswidget")