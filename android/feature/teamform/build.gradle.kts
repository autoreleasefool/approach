plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.teamform"
}

dependencies {
	implementation(projects.feature.teamform.ui)
}
