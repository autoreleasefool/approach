plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.accessoriesoverview"
}

dependencies {
	implementation(projects.feature.accessoriesoverview.ui)
	implementation(projects.feature.alleyslist)
	implementation(projects.feature.alleyslist.ui)
	implementation(projects.feature.gearlist)
	implementation(projects.feature.gearlist.ui)
}
