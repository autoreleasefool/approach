plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.seriesdetails.ui"
}

dependencies {
	implementation(projects.core.charts)
	implementation(projects.core.common)
	implementation(projects.core.model.charts.ui)
	implementation(projects.feature.gameslist.ui)

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
