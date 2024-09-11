plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.teamseriesdetails"
}

dependencies {
	implementation(projects.feature.gameslist.ui)
	implementation(projects.feature.teamseriesdetails.ui)

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
