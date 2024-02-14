plugins {
	id("approach.android.feature.ui")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.opponentslist.ui"
}

dependencies {
	implementation(project(":feature:bowlerslist:ui"))
}
