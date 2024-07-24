plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.overview"
}

dependencies {
	implementation(projects.core.statistics)
	implementation(projects.core.statistics.charts)
	implementation(projects.feature.bowlerslist.ui)
	implementation(projects.feature.overview.ui)
	implementation(projects.feature.statisticswidget.ui)

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
