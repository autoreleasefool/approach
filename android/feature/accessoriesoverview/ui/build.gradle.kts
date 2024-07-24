plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui"
}

dependencies {
	implementation(projects.feature.alleyslist.ui)
	implementation(projects.feature.gearlist.ui)
}
