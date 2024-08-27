plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.teamdetails"
}

dependencies {
	implementation(projects.feature.teamdetails.ui)

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
