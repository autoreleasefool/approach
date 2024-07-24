plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui"
}

dependencies {
	implementation(projects.feature.gearlist.ui)
	implementation(projects.feature.leagueslist.ui)
	implementation(projects.feature.statisticswidget.ui)

	implementation(libs.vico.compose)
}
