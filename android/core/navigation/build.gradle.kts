plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.navigation"
}

dependencies {
	implementation(project(":core:model"))
	implementation(project(":core:statistics"))

	implementation(libs.androidx.navigation.compose)
}
