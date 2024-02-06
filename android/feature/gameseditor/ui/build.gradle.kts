plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.gameseditor.ui"
}

dependencies {
	implementation(project(":core:scoresheet"))

	implementation(libs.compose.reorderable)
}