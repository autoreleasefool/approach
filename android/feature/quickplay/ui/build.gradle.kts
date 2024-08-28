plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.quickplay.ui"
}

dependencies {
	implementation(projects.core.common)

	implementation(libs.compose.reorderable)
	implementation(libs.kotlinx.datetime)
	implementation(libs.swipe)
}
