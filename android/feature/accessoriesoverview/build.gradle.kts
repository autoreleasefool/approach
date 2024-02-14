plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.accessoriesoverview"
}

dependencies {
	implementation(project(":feature:accessoriesoverview:ui"))
	implementation(project(":feature:alleyslist"))
	implementation(project(":feature:alleyslist:ui"))
	implementation(project(":feature:gearlist"))
	implementation(project(":feature:gearlist:ui"))
}
