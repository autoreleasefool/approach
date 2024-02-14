plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.leaguedetails.ui"
}

dependencies {
	implementation(project(":core:common"))
	implementation(project(":feature:serieslist:ui"))
	implementation(project(":feature:statisticswidget:ui"))

	implementation(libs.kotlinx.datetime)
}
