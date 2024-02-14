plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui"
}

dependencies {
	implementation(project(":core:model:ui"))
}
