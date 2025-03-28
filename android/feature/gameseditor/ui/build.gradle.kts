plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.gameseditor.ui"
}

dependencies {
	implementation(projects.core.scoresheet)
	implementation(projects.feature.sharing)
	implementation(projects.feature.sharing.ui)

	implementation(libs.reorderable.compose)
}
