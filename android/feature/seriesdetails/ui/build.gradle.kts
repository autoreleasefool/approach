plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.seriesdetails.ui"
}

dependencies {
	implementation(project(":core:charts"))
	implementation(project(":core:common"))
	implementation(project(":feature:gameslist:ui"))

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}