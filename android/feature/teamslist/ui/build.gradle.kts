plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.teamslist.ui"
}

dependencies {
	implementation(libs.swipe)
}
