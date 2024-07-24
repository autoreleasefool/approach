plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.opponentslist"
}

dependencies {
	implementation(projects.feature.opponentslist.ui)
}
