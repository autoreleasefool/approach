plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.seriesdetails"
}

dependencies {
	implementation(project(":feature:leaguedetails:ui"))
	implementation(project(":feature:serieslist:ui"))

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
