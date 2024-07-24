plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.seriesdetails"
}

dependencies {
	implementation(projects.feature.gameslist.ui)
	implementation(projects.feature.seriesdetails.ui)

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
