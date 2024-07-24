plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.scoresheet"
}

dependencies {
	implementation(projects.core.designsystem)
	implementation(projects.core.model)
}
