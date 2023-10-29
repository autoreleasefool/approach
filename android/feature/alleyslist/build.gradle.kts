plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.alleyslist"
}

dependencies {
	implementation(project(":feature:alleyslist:ui"))
}