plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.navigation"
}

dependencies {
	implementation(projects.core.model)
	implementation(projects.core.statistics)

	implementation(libs.androidx.navigation.compose)
}
