plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.statisticswidget.ui"
}

dependencies {
	implementation(project(":core:statistics"))
	implementation(project(":core:statistics:charts"))

	implementation(libs.compose.reorderable)
	implementation(libs.vico.compose)
}
