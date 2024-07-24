plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.leaguedetails.ui"
}

dependencies {
	implementation(projects.core.common)
	implementation(projects.feature.serieslist.ui)
	implementation(projects.feature.statisticswidget.ui)

	implementation(libs.kotlinx.datetime)
}
