plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.seriesform.ui"
}

dependencies {
	implementation(projects.core.common)
	implementation(libs.kotlinx.datetime)
}
