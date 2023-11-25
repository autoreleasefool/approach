plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui"
}

dependencies {
	implementation(project(":core:common"))
	implementation(project(":core:statistics"))

	implementation(libs.kotlinx.datetime)
}