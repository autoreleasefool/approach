plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.scoresheet"
}

dependencies {
	implementation(project(":core:designsystem"))
	implementation(project(":core:model"))
}
