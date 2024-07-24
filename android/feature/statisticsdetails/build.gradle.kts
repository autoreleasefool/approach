plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.statisticsdetails"
}

dependencies {
	implementation(projects.core.statistics)
	implementation(projects.core.statistics.charts)
	implementation(projects.feature.statisticsdetails.ui)

	implementation(libs.vico.compose)
}
