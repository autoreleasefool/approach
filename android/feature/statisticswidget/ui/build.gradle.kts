plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.statisticswidget.ui"
}

dependencies {
	implementation(projects.core.charts)
	implementation(projects.core.statistics)
	implementation(projects.core.statistics.charts)

	implementation(libs.reorderable.compose)
	implementation(libs.vico.compose)
}
