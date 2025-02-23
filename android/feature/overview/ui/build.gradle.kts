plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.overview.ui"
}

dependencies {
	implementation(projects.feature.bowlerslist.ui)
	implementation(projects.feature.statisticswidget.ui)
	implementation(projects.feature.teamslist.ui)

	implementation(libs.reorderable.compose)
	implementation(libs.swipe)
	implementation(libs.vico.compose)
}
