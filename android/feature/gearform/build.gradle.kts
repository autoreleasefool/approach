plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.gearform"
}

dependencies {
	implementation(projects.feature.gearform.ui)
}
