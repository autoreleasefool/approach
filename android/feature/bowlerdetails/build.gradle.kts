plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.bowlerdetails"
}

dependencies {
	implementation(projects.core.statistics)
	implementation(projects.core.statistics.charts)
	implementation(projects.feature.bowlerdetails.ui)
	implementation(projects.feature.leagueslist.ui)
	implementation(projects.feature.gearlist.ui)
	implementation(projects.feature.statisticswidget.ui)

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
