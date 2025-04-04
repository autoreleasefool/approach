plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.teamform.ui"
}

dependencies {
	implementation(projects.core.common)

	implementation(libs.reorderable.compose)
	implementation(libs.swipe)
}
