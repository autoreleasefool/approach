plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.overview"
}

dependencies {
	implementation(project(":core:statistics"))
	implementation(project(":feature:bowlerslist:ui"))
	implementation(project(":feature:overview:ui"))
	implementation(project(":feature:statisticswidget:ui"))
}