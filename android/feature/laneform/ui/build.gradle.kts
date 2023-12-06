plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.laneform.ui"
}

dependencies {
	implementation(project(":feature:laneslist:ui"))
}