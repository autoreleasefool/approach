plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.statisticsoverview"
}

dependencies {
	implementation(projects.core.statistics)
	implementation(projects.feature.statisticsoverview.ui)
}
