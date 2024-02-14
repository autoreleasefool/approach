plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.alleyform"
}

dependencies {
	implementation(project(":feature:alleyform:ui"))
}
