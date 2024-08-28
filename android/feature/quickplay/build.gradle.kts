plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.quickplay"
}

dependencies {
	implementation(projects.feature.quickplay.ui)

	implementation(libs.kotlinx.datetime)
}
