plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.overview"
}

dependencies {
	implementation(project(":core:statistics"))
	implementation(project(":core:statistics:charts"))
	implementation(project(":feature:bowlerslist:ui"))
	implementation(project(":feature:overview:ui"))
	implementation(project(":feature:statisticswidget:ui"))

	implementation(libs.kotlinx.datetime)
	implementation(libs.vico.compose)
}
