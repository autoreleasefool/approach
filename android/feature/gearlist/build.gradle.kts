plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.gearlist"
}

dependencies {
	implementation(projects.feature.gearlist.ui)
}
