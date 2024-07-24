plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.matchplayeditor"
}

dependencies {
	implementation(projects.feature.matchplayeditor.ui)
}
