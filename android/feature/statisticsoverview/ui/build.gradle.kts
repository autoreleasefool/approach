plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui"
}

dependencies {
	implementation(projects.core.common)
	implementation(projects.core.statistics)

	implementation(libs.kotlinx.datetime)
}
