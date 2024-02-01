plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui"
}

dependencies {
	implementation(project(":feature:alleyslist:ui"))
	implementation(project(":feature:gearlist:ui"))
}