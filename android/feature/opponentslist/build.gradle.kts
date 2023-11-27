plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.opponentslist"
}

dependencies {
	implementation(project(":feature:opponentslist:ui"))
}