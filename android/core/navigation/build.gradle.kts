plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
	id("approach.android.hilt")
	id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.navigation"
}

dependencies {
	implementation(projects.core.model)
	implementation(projects.core.statistics)

	implementation(libs.hilt.android)
	implementation(libs.androidx.navigation.compose)
}
