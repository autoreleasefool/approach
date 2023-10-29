plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.analytics"
}

dependencies {
	implementation(project(":feature:analytics:ui"))
}