plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.laneform"
}

dependencies {
	implementation(projects.feature.laneform.ui)
}
