plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.gameseditor"
}

dependencies {
	implementation(project(":feature:gameseditor:ui"))
}