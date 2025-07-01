plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.teamdetails"
}

dependencies {
	implementation(projects.core.charts)
	implementation(projects.feature.teamdetails.ui)

	implementation(libs.kotlinx.datetime)
}
