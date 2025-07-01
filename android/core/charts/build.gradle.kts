plugins {
	id("approach.android.library")
	id("approach.android.library.compose")
}

android {
	namespace = "ca.josephroque.bowlingcompanion.core.charts"
}

dependencies {
	implementation(projects.core.designsystem)

	api(libs.vico.compose)
	api(libs.vico.compose.m3)
}
