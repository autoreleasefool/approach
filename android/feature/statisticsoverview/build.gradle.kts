plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.statisticsoverview"
}

dependencies {
	implementation(project(":core:statistics"))
	implementation(project(":feature:statisticsoverview:ui"))
}