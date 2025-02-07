plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.leaguedetails"
}

dependencies {
	implementation(projects.feature.leaguedetails.ui)
	implementation(projects.feature.serieslist.ui)

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
