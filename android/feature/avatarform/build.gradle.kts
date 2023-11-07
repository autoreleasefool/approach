plugins {
	id("approach.android.feature")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.feature.avatarform"
}

dependencies {
	implementation(project(":core:model:ui"))
	implementation(project(":feature:avatarform:ui"))
}