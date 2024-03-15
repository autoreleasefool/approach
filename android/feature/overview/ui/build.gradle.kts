plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.overview.ui"
}

dependencies {
	implementation(project(":feature:bowlerslist:ui"))
	implementation(project(":feature:statisticswidget:ui"))

	implementation(libs.compose.reorderable)
	implementation(libs.swipe)
	implementation(libs.vico.compose)
}
